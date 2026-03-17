# serializers.py
from rest_framework import serializers
from django.contrib.auth.models import User
from .models import Employees, Positions
from django.utils import timezone
import random
import string

class UserRegistrationSerializer(serializers.ModelSerializer):
    password1 = serializers.CharField(write_only=True, required=True, style={'input_type': 'password'})
    password2 = serializers.CharField(write_only=True, required=True, style={'input_type': 'password'})
    email = serializers.EmailField(required=True)
    
    # Поля для модели Employees
    full_name = serializers.CharField(write_only=True, required=True, source='Full_Name')
    date_of_birth = serializers.DateField(write_only=True, required=True, source='Date_of_Birth')
    phone_number = serializers.CharField(write_only=True, required=True, source='Phone_Number')
    position_id = serializers.PrimaryKeyRelatedField(
        write_only=True, 
        required=True,
        queryset=Positions.objects.all(),
        source='Position_ID'
    )
    
    class Meta:
        model = User
        fields = [
            'full_name', 'email', 'password1', 'password2', 'date_of_birth', 'phone_number', 'position_id'
        ]
    
    def validate(self, data):
        # Проверяем совпадение паролей
        if data['password1'] != data['password2']:
            raise serializers.ValidationError({"password2": "Пароли не совпадают"})
        
        # Проверяем уникальность email в модели Employees
        if Employees.objects.filter(Email=data.get('email')).exists():
            raise serializers.ValidationError({"email": "Сотрудник с таким email уже существует"})
        
        return data
    
    def create(self, validated_data):
        # Извлекаем данные для Employees
        full_name = validated_data.pop('full_name')
        date_of_birth = validated_data.pop('date_of_birth')
        phone_number = validated_data.pop('phone_number')
        position = validated_data.pop('position_id')  # position_id из validated_data
        username=full_name
        # Извлекаем пароли
        password = validated_data.pop('password1')
        validated_data.pop('password2')
        email = validated_data.get('email')
        
        base_username = full_name.lower().replace(' ', '_')[:20]
        username = base_username
        
        if User.objects.filter(username=base_username).exists():
            while True:
                random_number = random.randint(100, 999)
                username = f"{base_username}_{random_number}"
                if not User.objects.filter(username=username).exists():
                    break
        # Создаем пользователя Django
        user = User.objects.create_user(
            username=username,
            email=email,
            password=password
        )
        
        # Создаем запись в Employees, связывая с созданным пользователем
        employee = Employees.objects.create(
            user=user,
            Full_Name=full_name,
            Date_of_Birth=date_of_birth,
            Phone_Number=phone_number,
            Email=email,
            Position_ID=Positions.objects.get(ID=position)
        )
        
        return user
    
