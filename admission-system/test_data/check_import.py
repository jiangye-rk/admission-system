import pymysql

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'admission_db',
    'charset': 'utf8mb4'
}

def check_import_status():
    """检查数据导入状态"""
    try:
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        print("=" * 60)
        print("录取数据导入情况检查")
        print("=" * 60)
        
        # 1. 检查总记录数
        cursor.execute("SELECT COUNT(*) FROM admission_data")
        total_count = cursor.fetchone()[0]
        print(f"\n总记录数: {total_count} 条")
        
        if total_count == 0:
            print("\n警告: 数据库中没有录取数据！")
            print("请先在系统中执行数据导入操作。")
            return
        
        # 2. 按年份统计
        print("\n【按年份统计】")
        cursor.execute("SELECT nf, COUNT(*) FROM admission_data GROUP BY nf ORDER BY nf")
        year_stats = cursor.fetchall()
        for year, count in year_stats:
            print(f"  {year}年: {count} 条")
        
        # 3. 按批次统计
        print("\n【按批次统计】")
        cursor.execute("SELECT pcmc, COUNT(*) FROM admission_data GROUP BY pcmc ORDER BY COUNT(*) DESC")
        batch_stats = cursor.fetchall()
        for batch, count in batch_stats:
            print(f"  {batch}: {count} 条")
        
        # 4. 院校数量
        print("\n【院校统计】")
        cursor.execute("SELECT COUNT(DISTINCT yxdm) FROM admission_data")
        college_count = cursor.fetchone()[0]
        print(f"  院校总数: {college_count} 所")
        
        # 5. 985/211统计
        print("\n【985/211统计】")
        cursor.execute("SELECT sf985, COUNT(*) FROM admission_data WHERE sf985 = 1")
        count_985 = cursor.fetchone()[1]
        cursor.execute("SELECT sf211, COUNT(*) FROM admission_data WHERE sf211 = 1")
        count_211 = cursor.fetchone()[1]
        print(f"  985院校数据: {count_985} 条")
        print(f"  211院校数据: {count_211} 条")
        
        # 6. 显示部分数据示例
        print("\n【数据示例（前5条）】")
        cursor.execute("""
            SELECT nf, yxmc, zymc, zgf, zdf, pjf, zdfwc 
            FROM admission_data 
            LIMIT 5
        """)
        samples = cursor.fetchall()
        print(f"{'年份':<6} {'院校名称':<20} {'专业名称':<20} {'最高分':<8} {'最低分':<8} {'平均分':<8} {'位次':<10}")
        print("-" * 90)
        for row in samples:
            print(f"{row[0]:<6} {row[1]:<20} {row[2]:<20} {row[3]:<8} {row[4]:<8} {row[5]:<8} {row[6]:<10}")
        
        # 7. 检查是否有异常数据
        print("\n【数据质量检查】")
        cursor.execute("SELECT COUNT(*) FROM admission_data WHERE zgf IS NULL OR zdf IS NULL")
        null_score_count = cursor.fetchone()[0]
        if null_score_count > 0:
            print(f"  警告: {null_score_count} 条记录分数为空")
        else:
            print("  分数数据完整性: 正常")
        
        cursor.execute("SELECT COUNT(*) FROM admission_data WHERE yxmc IS NULL OR yxmc = ''")
        null_college_count = cursor.fetchone()[0]
        if null_college_count > 0:
            print(f"  警告: {null_college_count} 条记录院校名称为空")
        else:
            print("  院校名称完整性: 正常")
        
        print("\n" + "=" * 60)
        
        cursor.close()
        conn.close()
        
    except Exception as e:
        print(f"检查失败: {e}")
        print("\n请确保:")
        print("1. MySQL 服务已启动")
        print("2. 数据库 admission_db 已创建")
        print("3. 数据库用户名密码正确")

if __name__ == "__main__":
    check_import_status()
