import zipfile
import os

# 解压 docx 文件（docx 实际上是 zip 格式）
with zipfile.ZipFile('说明.docx', 'r') as zip_ref:
    zip_ref.extractall('docx_extracted')

# 读取 document.xml
doc_path = 'docx_extracted/word/document.xml'
if os.path.exists(doc_path):
    with open(doc_path, 'r', encoding='utf-8') as f:
        content = f.read()
    print("文件内容长度:", len(content))
    print("\n内容预览:")
    print(content[:5000])
else:
    print("document.xml 不存在")
    # 列出所有文件
    for root, dirs, files in os.walk('docx_extracted'):
        for file in files:
            print(os.path.join(root, file))
