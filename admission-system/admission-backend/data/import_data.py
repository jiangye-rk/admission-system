import pandas as pd
import pymysql
from sqlalchemy import create_engine
import os

# 数据库配置 - 请根据你的实际配置修改
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',  # 修改为你的密码
    'database': 'admission_db',
    'charset': 'utf8mb4'
}

def get_connection():
    """获取数据库连接"""
    return pymysql.connect(**DB_CONFIG)

def get_engine():
    """获取SQLAlchemy引擎"""
    return create_engine(
        f"mysql+pymysql://{DB_CONFIG['user']}:{DB_CONFIG['password']}@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}?charset=utf8mb4"
    )

def import_score_segment():
    """导入一分一段表数据"""
    print("=" * 50)
    print("开始导入一分一段表数据...")
    
    # 获取当前脚本所在目录
    base_dir = os.path.dirname(os.path.abspath(__file__))
    csv_path = os.path.join(base_dir, 'yfydb2025.csv')
    
    # 读取CSV文件
    df = pd.read_csv(csv_path, encoding='gbk')
    df.columns = ['fs', 'tfrs', 'ljrs']  # 重命名列
    df['nf'] = 2025  # 添加年份
    
    # 导入数据库
    engine = get_engine()
    df.to_sql('score_segment', engine, if_exists='append', index=False)
    
    print(f"成功导入 {len(df)} 条一分一段数据")
    print("=" * 50)

def import_admission_data():
    """导入录取数据"""
    print("=" * 50)
    print("开始导入录取数据...")
    
    # 需要导入的文件列表
    files = [
        ('T2021.xlsx', 2021),
        ('T2022.xlsx', 2022),
        ('T2023.xlsx', 2023),
        ('T2024.xls', 2024),
        ('T2025.xls', 2025)
    ]
    
    engine = get_engine()
    total_count = 0
    
    for filename, year in files:
        filepath = os.path.join(os.path.dirname(__file__), filename)
        if not os.path.exists(filepath):
            print(f"文件不存在: {filename}, 跳过")
            continue
            
        print(f"\n正在导入 {filename} ({year}年)...")
        
        # 读取Excel文件
        df = pd.read_excel(filepath)
        
        # 列名映射（Excel列名 -> 数据库列名）
        # 注意：不同年份的列名可能不同，2021年使用"专业最低分"，2022-2025年使用"组最低分"
        column_mapping = {
            '年份': 'nf',
            '批次名称': 'pcmc',
            '院校代码': 'yxdm',
            '院校名称': 'yxmc',
            '选考科目': 'kskmyq',
            '专业代码': 'zydm',
            '专业名称': 'zymc',
            '组最高分': 'zgf',
            '组最低分': 'zdf',
            '组最低位次': 'zdfwc',
            '组平均分': 'pjf',
            '专业计划': 'zjhs',
            '专业录取': 'lqs',
            '专业最低分': 'zyzdf',
            '专业最低位次': 'zyzdfwc',
            '专业平均分': 'zypjf',
            '组平均位次': 'zpjwc',
            '组中位分': 'zzwf',
            '组中位位次': 'zzwwc',
            '专业平均位次': 'zypjwc',
            '专业中位分': 'zyzwf',
            '专业中位位次': 'zyzwwc'
        }
        
        # 重命名列
        df = df.rename(columns=column_mapping)
        
        # 处理不同年份的列名差异
        # 如果存在"专业最低分"但没有"组最低分"（如2021年），将专业最低分复制到组最低分
        if 'zyzdf' in df.columns and 'zdf' not in df.columns:
            df['zdf'] = df['zyzdf']
        if 'zyzdfwc' in df.columns and 'zdfwc' not in df.columns:
            df['zdfwc'] = df['zyzdfwc']
        if 'zypjf' in df.columns and 'pjf' not in df.columns:
            df['pjf'] = df['zypjf']
        
        # 统一专业名称格式（去掉括号及后面的内容）
        # 例如："工商管理类（工商管理(智能化与创新管理)、公共事业管理..." -> "工商管理类"
        if 'zymc' in df.columns:
            df['zymc'] = df['zymc'].str.split('（').str[0].str.split('(').str[0].str.strip()
        
        # 确保年份正确
        df['nf'] = year
        
        # 添加默认值
        df['sf985'] = 0
        df['sf211'] = 0
        df['ssmc'] = '天津'
        df['sfbz'] = ''
        
        # 选择数据库中存在的列
        columns_needed = [
            'nf', 'pcmc', 'yxdm', 'yxmc', 'zydm', 'zymc', 'kskmyq',
            'zgf', 'zdf', 'zdfwc', 'pjf', 'zpjwc', 'zzwf', 'zzwwc',
            'zjhs', 'lqs', 'zyzdf', 'zyzdfwc', 'zypjf', 'zypjwc', 'zyzwf', 'zyzwwc',
            'sf985', 'sf211', 'ssmc', 'sfbz'
        ]
        
        # 只保留存在的列
        existing_columns = [col for col in columns_needed if col in df.columns]
        df = df[existing_columns]
        
        # 导入数据库
        df.to_sql('admission_data', engine, if_exists='append', index=False)
        
        print(f"  成功导入 {len(df)} 条数据")
        total_count += len(df)
    
    print(f"\n总共导入 {total_count} 条录取数据")
    print("=" * 50)

def main():
    """主函数"""
    print("\n" + "=" * 50)
    print("高校录取数据导入工具")
    print("=" * 50 + "\n")
    
    try:
        # 导入一分一段表
        import_score_segment()
        
        # 导入录取数据
        import_admission_data()
        
        print("\n[OK] 所有数据导入完成！")
        
    except Exception as e:
        print(f"\n[ERROR] 导入失败: {e}")
        import traceback
        traceback.print_exc()

if __name__ == '__main__':
    main()
