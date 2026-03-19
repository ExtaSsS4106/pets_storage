import eel
import os, requests, json
from .settings import *
from .render import render

class Views:

    @eel.expose
    def GET_HOST():
        return HOST
    
    
    @eel.expose
    def main():
        with open(USER_DATA, 'r', encoding='utf-8') as file:
            data = json.load(file)
            print(data)
        return render("web/pages/main/main.html", data)

    @eel.expose
    def add_page():
        with open(USER_DATA, 'r', encoding='utf-8') as file:
            data = json.load(file)
            print(data)
        return render("web/pages/main/add.html", data) 

    @eel.expose
    def get_load_page():
        try:
            with open(CONF_PATH, 'r', encoding='utf-8') as file:
                data = json.load(file)
            return data['load_page']
        except Exception as e:
            print(f"[ERROR]: {e}")
            
            
    @eel.expose
    def set_load_page(value='login'):
        try:
            with open(CONF_PATH, 'r', encoding='utf-8') as f:
                data = json.load(f)
                    
                # Изменяем
                data['load_page'] = value
                    
            # Записываем
            with open(CONF_PATH, 'w', encoding='utf-8') as f:
                json.dump(data, f, indent=4, ensure_ascii=False)
                    
            return True
        except Exception as e:
            print(f"[ERROR]: {e}")
            
    
    
    
    
    
    
    @eel.expose
    def select_products():
        try:
            with open(USER_DATA, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            response = requests.get(
                url=f"{HOST}select_all",
                headers={
                    'Content-Type': 'application/json',
                    'Authorization': f'Token {data["token"]}'
                }
            )
            
            if response.status_code == 200:
                response_data = response.json()
                #print(response_data.get('data', []))
                if not response_data['data'] == 'empty':
                    return response_data['data']['data']
                else:
                    return response_data['data']
            else:
                return {'error': True, 'message': f'Ошибка {response.status_code}'}
                
        except Exception as e:
            print(f"Ошибка: {e}")
            return {'error': True, 'message': str(e)}
        
    @eel.expose
    def add_products(P_data):
        try:
            with open(USER_DATA, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            response = requests.post(
                url=f"{HOST}add",
                data=json.dumps(P_data),
                headers={
                    'Content-Type': 'application/json',
                    'Authorization': f'Token {data["token"]}'
                }
            )
            
            if response.status_code == 200:
                response_data = response.json()
                #print(response_data.get('data', []))
                if not response_data['data'] == 'empty':
                    return response_data['data']['data']
                else:
                    return response_data['data']
            else:
                return {'error': True, 'message': f'Ошибка {response.status_code}'}
                
        except Exception as e:
            print(f"Ошибка: {e}")
            return {'error': True, 'message': str(e)}
        
    @eel.expose
    def select_movment():
        try:
            with open(USER_DATA, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            response = requests.get(
                url=f"{HOST}select_movment",
                headers={
                    'Content-Type': 'application/json',
                    'Authorization': f'Token {data["token"]}'
                }
            )
            
            if response.status_code == 200:
                response_data = response.json()
                #print(response_data.get('data', []))
                if not response_data['data'] == 'empty':
                    return response_data['data']['data']
                else:
                    return response_data['data']
            else:
                return {'error': True, 'message': f'Ошибка {response.status_code}'}
                
        except Exception as e:
            print(f"Ошибка: {e}")
            return {'error': True, 'message': str(e)}

        
    @eel.expose
    def select_employees():
        try:
            with open(USER_DATA, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            response = requests.get(
                url=f"{HOST}select_employees",
                headers={
                    'Content-Type': 'application/json',
                    'Authorization': f'Token {data["token"]}'
                }
            )
            
            if response.status_code == 200:
                response_data = response.json()
                print(response_data.get('data', []))
                if not response_data['data'] == 'empty':
                    return response_data['data']['data']
                else:
                    return response_data['data']
            else:
                return {'error': True, 'message': f'Ошибка {response.status_code}'}
                
        except Exception as e:
            print(f"Ошибка: {e}")
            return {'error': True, 'message': str(e)}


    @eel.expose
    def select_expired():
        try:
            with open(USER_DATA, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            response = requests.get(
                url=f"{HOST}select_expired",
                headers={
                    'Content-Type': 'application/json',
                    'Authorization': f'Token {data["token"]}'
                }
            )
            print(response.status_code)
            if response.status_code == 200:
                response_data = response.json()
                print(response_data)
                if not response_data['data'] == 'empty':
                    return response_data['data']['data']
                else:
                    return response_data['data']
            else:
                return {'error': True, 'message': f'Ошибка {response.status_code}'}
                
        except Exception as e:
            print(f"Ошибка: {e}")
            return {'error': True, 'message': str(e)}


    @eel.expose
    def select_deleted():
        try:
            with open(USER_DATA, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            response = requests.get(
                url=f"{HOST}select_deleted",
                headers={
                    'Content-Type': 'application/json',
                    'Authorization': f'Token {data["token"]}'
                }
            )
            
            if response.status_code == 200:
                response_data = response.json()
                if not response_data['data'] == 'empty':
                    return response_data['data']['data']
                else:
                    return response_data['data']
            else:
                return {'error': True, 'message': f'Ошибка {response.status_code}'}
                
        except Exception as e:
            print(f"Ошибка: {e}")
            return {'error': True, 'message': str(e)}


    @eel.expose
    def select_movment_by_action(action):
        try:
            with open(USER_DATA, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            response = requests.get(
                url=f"{HOST}select_movment/{action}",
                headers={
                    'Content-Type': 'application/json',
                    'Authorization': f'Token {data["token"]}'
                }
            )
            
            if response.status_code == 200:
                response_data = response.json()
                if not response_data['data'] == 'empty':
                    return response_data['data']['data']
                else:
                    return response_data['data']
            else:
                return {'error': True, 'message': f'Ошибка {response.status_code}'}
                
        except Exception as e:
            print(f"Ошибка: {e}")
            return {'error': True, 'message': str(e)}


    @eel.expose
    def select_employees_by_position(position_id):
        try:
            with open(USER_DATA, 'r', encoding='utf-8') as file:
                data = json.load(file)
            
            response = requests.get(
                url=f"{HOST}select_employees/{position_id}",
                headers={
                    'Content-Type': 'application/json',
                    'Authorization': f'Token {data["token"]}'
                }
            )
            print(response.status_code)
            if response.status_code == 200:
                response_data = response.json()
                if not response_data['data'] == 'empty':
                    return response_data['data']['data']
                else:
                    return response_data['data']
            else:
                return {'error': True, 'message': f'Ошибка {response.status_code}'}
                
        except Exception as e:
            print(f"Ошибка: {e}")
            return {'error': True, 'message': str(e)}
        
        
        
    
    @eel.expose
    def login(email=None, passwd=None):
        if email and passwd:
            print(email, passwd)
            r = requests.post(
                    url=f"{HOST}login",
                    data=json.dumps({ 
                        "email": email,
                        "password": passwd
                    }),
                    headers={
                        'Content-Type': 'application/json'
                    }
                )
            print(r.status_code)
            if r.status_code == 200:
                with open(USER_DATA, "w", encoding="utf-8") as f:
                    json.dump(r.json(), f, indent=4, ensure_ascii=False)
                return {'status': str(r.status_code)}
            return {'status': str(r.status_code)}
        else:
            return render("web/pages/registration/login.html")
    
    
    @eel.expose
    def sign_up(username=None,email=None,password1=None,password2=None):
        if username and email and password1 and password2:
            print(username,email,password1,password2)
            r = requests.post(
                    url=f"{HOST}sign_up",
                    data=json.dumps({
                        "username": username,
                        "email": email,
                        "password1": password1,
                        "password2": password2
                    }),
                    headers={
                        'Content-Type': 'application/json'
                    }
                )
            print(r.status_code)
            print(r.json())
            if r.status_code == 200:
                with open(USER_DATA, "w", encoding="utf-8") as f:
                    json.dump(r.json(), f, indent=4, ensure_ascii=False)
                return True
        else:
            return render("web/pages/registration/register.html")