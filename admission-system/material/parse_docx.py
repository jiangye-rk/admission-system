import zipfile
import xml.etree.ElementTree as ET
import re

# 解压并读取 docx
def extract_text_from_docx(docx_path):
    with zipfile.ZipFile(docx_path, 'r') as zip_ref:
        # 读取 document.xml
        with zip_ref.open('word/document.xml') as f:
            xml_content = f.read()

    # 解析 XML
    root = ET.fromstring(xml_content)

    # Word 命名空间
    namespaces = {
        'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'
    }

    # 提取所有文本
    text_parts = []
    for elem in root.iter():
        if elem.tag == '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}t':
            if elem.text:
                text_parts.append(elem.text)

    return ''.join(text_parts)

# 清理文本
def clean_text(text):
    # 移除多余空白
    text = re.sub(r'\s+', ' ', text)
    return text.strip()

# 执行提取
docx_path = '说明.docx'
text = extract_text_from_docx(docx_path)
cleaned_text = clean_text(text)

print("=" * 80)
print("文档内容:")
print("=" * 80)
print(cleaned_text)
print("\n" + "=" * 80)
print(f"总字符数: {len(cleaned_text)}")
