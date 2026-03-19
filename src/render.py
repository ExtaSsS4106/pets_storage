import eel
import os

    
    
def render(file_path=None, data=None):
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            content = file.read()
        if file_path and data:
            return content, data
        elif file_path and data == None:
            return content
        elif data and file_path == None:
            return data
        else:
            raise ValueError("Не переданы ни file_path, ни data") 
    except FileNotFoundError:
        print(f"Файл {file_path} не найден")
    except Exception as e:
        print(e)       
