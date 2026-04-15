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

def delete_test_data():
    """删除测试数据"""
    try:
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        print("=" * 60)
        print("删除测试数据")
        print("=" * 60)
        
        # 先查看当前数据情况
        cursor.execute("SELECT COUNT(*) FROM admission_data")
        total_before = cursor.fetchone()[0]
        print(f"\n删除前总记录数: {total_before} 条")
        
        # 查看测试数据中的院校（我们生成的30所院校）
        test_colleges = [
            "10001", "10002", "10003", "10004", "10005", "10006", "10007", 
            "10008", "10010", "10013", "10019", "10022", "10027", "10030", 
            "10033", "10034", "10036", "10043", "10052", "10053", "10054", 
            "10055", "10056", "10057", "10058", "10059", "10060", "10061", 
            "10062", "10063"
        ]
        
        # 检查这些院校在2023和2024年的数据量
        format_strings = ','.join(['%s'] * len(test_colleges))
        cursor.execute(f"""
            SELECT nf, COUNT(*) 
            FROM admission_data 
            WHERE yxdm IN ({format_strings}) AND nf IN (2023, 2024)
            GROUP BY nf
        """, test_colleges)
        
        test_data_stats = cursor.fetchall()
        print("\n【测试数据统计】")
        test_data_count = 0
        for year, count in test_data_stats:
            print(f"  {year}年测试数据: {count} 条")
            test_data_count += count
        
        if test_data_count == 0:
            print("\n没有找到测试数据（2023-2024年的30所测试院校数据）")
            print("可能数据已经被删除，或者测试数据使用了不同的年份/院校代码")
            
            # 询问用户是否要查看所有年份的数据分布
            cursor.execute("""
                SELECT nf, COUNT(*) 
                FROM admission_data 
                WHERE yxdm IN ({format_strings})
                GROUP BY nf
            """.format(format_strings), test_colleges)
            
            all_test_stats = cursor.fetchall()
            if all_test_stats:
                print("\n这些院校在所有年份的数据分布:")
                for year, count in all_test_stats:
                    print(f"  {year}年: {count} 条")
            
            cursor.close()
            conn.close()
            return
        
        print(f"\n将要删除的测试数据: {test_data_count} 条")
        
        # 自动确认删除
        confirm = 'yes'
        print("自动确认删除...")
        
        if confirm.lower() == 'yes':
            # 执行删除
            cursor.execute(f"""
                DELETE FROM admission_data 
                WHERE yxdm IN ({format_strings}) AND nf IN (2023, 2024)
            """.format(format_strings), test_colleges)
            
            deleted_count = cursor.rowcount
            conn.commit()
            
            print(f"\n成功删除 {deleted_count} 条测试数据")
            
            # 查看删除后的数据量
            cursor.execute("SELECT COUNT(*) FROM admission_data")
            total_after = cursor.fetchone()[0]
            print(f"删除后总记录数: {total_after} 条")
            print(f"实际减少: {total_before - total_after} 条")
        else:
            print("\n已取消删除操作")
        
        print("=" * 60)
        
        cursor.close()
        conn.close()
        
    except Exception as e:
        print(f"删除失败: {e}")
        print("\n请确保:")
        print("1. MySQL 服务已启动")
        print("2. 数据库 admission_db 已创建")
        print("3. 数据库用户名密码正确")

if __name__ == "__main__":
    delete_test_data()
