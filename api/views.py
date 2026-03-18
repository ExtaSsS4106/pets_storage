from django.contrib.auth.models import User

from rest_framework.authentication import SessionAuthentication, TokenAuthentication
from rest_framework.decorators import api_view, permission_classes, authentication_classes
from rest_framework.response import Response
from rest_framework.permissions import AllowAny, IsAuthenticated

from .serializers import UserRegistrationSerializer
from django.contrib.auth import login, logout
from django.shortcuts import get_object_or_404
from django.views.decorators.csrf import csrf_exempt
from django.db.models import Q
from django.http import HttpResponse, JsonResponse

from rest_framework import status
from rest_framework.authtoken.models import Token
import json

from .models import *

@csrf_exempt
@api_view(['POST'])
@permission_classes([AllowAny])
@authentication_classes([]) 
def login_api(request):
    """
    {
        "email": "your_email",
        "password": "your_passwd"
    }
    """
    print("DATA",request.data['email'], request.data['password'])
    user = get_object_or_404(User, email=request.data['email'])
    serializer = UserRegistrationSerializer(instance=user)
    if not user.check_password(request.data['password']):
        return Response({'detail':'wrong password'}, status=status.HTTP_404_NOT_FOUND)
    token, created=Token.objects.get_or_create(user=user)
    
    return Response({"token": token.key, "user":serializer.data})

@api_view(['POST'])
@permission_classes([AllowAny])
def sign_up_api(request):
    """
        {
            "full_name": full_Name,
            "email": email,
            "password1": password1,
            "password2": password2,
            
            "date_of_birth": date_of_birth,
            "phone_number": phone_number,
            "position_id": position_id
        }
    """
    serializer = UserRegistrationSerializer(data=request.data)
    if serializer.is_valid():
        user = serializer.create(request.data)
        login(request, user)
        token = Token.objects.create(user=user)
        return Response({"token": token.key, "user":serializer.data})
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def logout_api(request):
    try:
        request.user.auth_token.delete()
        return Response({'detail': 'Успешный выход'})
    except:
        return Response({'detail': 'Выполнен выход'})
 
@api_view(['POST', 'GET'])
@permission_classes([IsAuthenticated])
def add(request):
    """
        "new_products":[{
            "name": product_name,
            "manufacturer": product_manufacturer,
            "category_id": product_category_id,
            "animal_type_id": product_animal_type_id,
            "expiry_date": product_expiry_date,
            "delivery_date": product_delivery_date,
        }]
    """
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            if data:
                new_products = data.get('new_products')#!!! must to be array
                errors = []
                for np in new_products:
                    name = np.get('name')
                    manufacturer = np.get('manufacturer')
                    category_id = np.get('category_id')
                    animal_type_id = np.get('animal_type_id')
                    expiry_date = np.get('expiry_date')
                    delivery_date = np.get('delivery_date')
                    if name and manufacturer and category_id and animal_type_id and expiry_date and delivery_date:
                        product = Products_in_storage.objects.create(Name=name, Manufacturer=manufacturer, Category_ID_id=category_id, Animal_Type_ID_id=animal_type_id, Expiry_Date=expiry_date, Delivery_Date=delivery_date)
                        Item_Movements.objects.create(Products_in_storage_ID=product, Action='adding', Employee_ID=Employees.objects.get(user=request.user))
                    else:
                        errors.append(np)
                        print(np, "not added")
                response = {
                    "errors_count": len(errors) ,
                    "list": errors,
                    "status": "not added"                    
                }
                return JsonResponse({"data": response})
            else:
                print("no data")
                return JsonResponse({"data": "no data"})
        except Exception as e:
            response = {
                "status": "error",
                "error": e,
            }
            return JsonResponse({"data": response})
    else:
        categoryes = Categories.objects.all()
        animal_types = Animal_Types.objects.all()
        response = {
            "categories": [{"id":category.ID, "type":category.Type} for category in categoryes],
            "animal_types": [{"id":animal_type.ID, "type":animal_type.Type} for animal_type in animal_types]
        }
        return JsonResponse({"data": response})
@api_view(['POST'])
@permission_classes([IsAuthenticated])
def delete(request):
    """
        "products":[{"id":product_id}]
    """
    try:
        data = json.loads(request.body)
        if data:
            products = data.get('products') #!!! must to be array
            errors = []
            for p in products:
                if Products_in_storage.objects.filter(ID=p.get('id')).exists():
                    product = Products_in_storage.objects.get(ID=p.get('id'))
                    product.Status = 'deleted'
                    product.save()
                    Item_Movements.objects.create(Products_in_storage_ID=product, Action='disposal', Employee_ID=Employees.objects.get(user=request.user))
                else:
                    errors.append(p)
            response = {
                "errors_count": len(errors),
                "list": errors,
                "status": "not added"                    
            }
            return JsonResponse({"data": response})
        else:
            print("no data")
            return JsonResponse({"data": "no data"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})
    
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def select_all(request):
    try:
        products = Products_in_storage.objects.all().order_by('-ID')
        if products.exists():
            response = {
                "status": "all_products",
                "data": [{"id":p.ID, "name":p.Name, "manufacturer":p.Manufacturer, "category":p.Category_ID.Type, "animal_type": p.Animal_Type_ID.Type, "expiry_date":p.Expiry_Date, "delivery_date":p.Delivery_Date, "status":p.Status} for p in products],
            }
            return JsonResponse({"data": response})
        else:
            return JsonResponse({"data": "empty"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def select_movment(request):
    try:
        movments = Item_Movements.objects.all()
        if movments.exists():
            response = {
                "status": "all_movments",
                "data": [{"id":m.ID, "name":m.Products_in_storage_ID.Name, "manufacturer":m.Products_in_storage_ID.Manufacturer, "p_id":m.Products_in_storage_ID.ID, "action":m.Action, "date_time": m.Date_Time, "description":m.Description, "employee":m.Employee_ID.Full_Name, "employee_email":m.Employee_ID.Email} for m in movments],
            }
            return JsonResponse({"data": response})
        else:
            return JsonResponse({"data": "empty"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})
    
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def select_employees(request):
    try:
        employees = Employees.objects.all()
        if employees.exists():
            response = {
                "status": "all_employees",
                "data": [{"id":e.ID, "full_name":e.Full_Name, "date_of_birth":e.Date_of_Birth, "phone_number":e.Phone_Number, "email": e.Email, "position":e.Position_ID.Type, "position_id": e.Position_ID.ID} for e in employees],
            }
            return JsonResponse({"data": response})
        else:
            return JsonResponse({"data": "empty"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def delete_employees(request):
    try:
        user = request.user
        user_profile = Employees.objects.get(user=user)
        if user_profile.Position_ID.ID == 1:
            data = json.loads(request.body)
            uid = data.get('uid')
            if User.objects.filter(id=uid).exists():
                usr = User.objects.get(id=uid)
                usr.delete()
            else:
                return JsonResponse({"data": "user not found"})
        else:
            return JsonResponse({"data": "no permission acces"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def select_expired(request):
    try:
        from datetime import date
        products = Products_in_storage.objects.filter(Expiry_Date__lt=date.today(), Status='exists')
        if products.exists():
            response = {
                "status": "expired_products",
                "data": [{"id":p.ID, "name":p.Name, "manufacturer":p.Manufacturer, "category":p.Category_ID.Type, "animal_type": p.Animal_Type_ID.Type, "expiry_date":p.Expiry_Date, "delivery_date":p.Delivery_Date, "status":p.Status} for p in products],
            }
            return JsonResponse({"data": response})
        else:
            return JsonResponse({"data": "empty"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def select_deleted(request):
    try:
        products = Products_in_storage.objects.filter(Status='deleted')
        if products.exists():
            response = {
                "status": "deleted_products",
                "data": [{"id":p.ID, "name":p.Name, "manufacturer":p.Manufacturer, "category":p.Category_ID.Type, "animal_type": p.Animal_Type_ID.Type, "expiry_date":p.Expiry_Date, "delivery_date":p.Delivery_Date, "status":p.Status} for p in products],
            }
            return JsonResponse({"data": response})
        else:
            return JsonResponse({"data": "empty"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def select_movment_by_action(request, action):
    try:
        movments = Item_Movements.objects.filter(Action=action)
        if movments.exists():
            response = {
                "status": f"{action}_movments",
                "data": [{"id":m.ID, "name":m.Products_in_storage_ID.Name, "manufacturer":m.Products_in_storage_ID.Manufacturer, "p_id":m.Products_in_storage_ID.ID, "action":m.Action, "date_time": m.Date_Time, "description":m.Description, "employee":m.Employee_ID.Full_Name, "employee_email":m.Employee_ID.Email} for m in movments],
            }
            return JsonResponse({"data": response})
        else:
            return JsonResponse({"data": "empty"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def select_employees_by_position(request, position_id):
    try:
        employees = Employees.objects.filter(Position_ID_id=position_id)
        if employees.exists():
            response = {
                "status": f"employees_position_{position_id}",
                "data": [{"id":e.ID, "full_name":e.Full_Name, "date_of_birth":e.Date_of_Birth, "phone_number":e.Phone_Number, "email": e.Email, "position":e.Position_ID.Type, "position_id": e.Position_ID.ID} for e in employees],
            }
            return JsonResponse({"data": response})
        else:
            return JsonResponse({"data": "empty"})
    except Exception as e:
        response = {
            "status": "error",
            "error": e,
        }
        return JsonResponse({"data": response})