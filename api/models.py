from django.db import models
from django.contrib.auth.models import User
# Create your models here.

class Animal_Types(models.Model):
    ID = models.AutoField(primary_key=True, db_column='ID')
    Type = models.TextField(db_column='Type')  # В дампе TEXT, не ENUM
    
    class Meta:
        
        db_table = 'Animal_Types'
    
    def __str__(self):
        return self.Type


class Categories(models.Model):
    ID = models.AutoField(primary_key=True, db_column='ID')
    Type = models.TextField(db_column='Type')  # В дампе TEXT, не ENUM
    
    class Meta:
        
        db_table = 'Categories'
    
    def __str__(self):
        return self.Type


class Positions(models.Model):
    ID = models.AutoField(primary_key=True, db_column='ID')
    Type = models.CharField(
        max_length=20, 
        choices=[('Administrator', 'Administrator'), ('Warehouse_Operator', 'Warehouse_Operator')],
        db_column='Type'
    )
    
    class Meta:
        
        db_table = 'Positions'
    
    def __str__(self):
        return self.Type


class Employees(models.Model):
    ID = models.AutoField(primary_key=True, db_column='ID')
    user = models.OneToOneField(User, on_delete=models.CASCADE, related_name='profile')
    Full_Name = models.CharField(max_length=255, db_column='Full_Name')
    Date_of_Birth = models.DateField(db_column='Date_of_Birth')
    Phone_Number = models.CharField(max_length=20, db_column='Phone_Number')
    Email = models.CharField(max_length=255, unique=True, db_column='Email')
    Position_ID = models.ForeignKey(
        'Positions', 
        on_delete=models.RESTRICT, 
        db_column='Position_ID',
        to_field='ID'
    )
    
    class Meta:
        
        db_table = 'Employees'
        indexes = [
            models.Index(fields=['Position_ID'], name='idx_employees_position'),
        ]
    
    def __str__(self):
        return self.Full_Name




class Products_in_storage(models.Model):
    ID = models.AutoField(primary_key=True, db_column='ID')
    Name = models.CharField(max_length=255, db_column='Name')
    Manufacturer = models.CharField(max_length=255, db_column='Manufacturer')
    Category_ID = models.ForeignKey(
        'Categories', 
        on_delete=models.RESTRICT, 
        db_column='Category_ID',
        to_field='ID'
    )
    Animal_Type_ID = models.ForeignKey(
        'Animal_Types', 
        on_delete=models.RESTRICT, 
        db_column='Animal_Type_ID',
        to_field='ID'
    )

    Expiry_Date = models.DateField(db_column='Expiry_Date')
    Delivery_Date = models.DateField(db_column='Delivery_Date')
    Status = models.CharField(
        max_length=10,
        choices=[('exists', 'exists'), ('expired', 'expired'), ('deleted', 'deleted')],
        db_column='Status',
        default='exists'
    )
    
    class Meta:
        
        db_table = 'Products_in_storage'
        indexes = [
            models.Index(fields=['Category_ID'], name='idx_products_category'),
            models.Index(fields=['Animal_Type_ID'], name='idx_products_animal'),
            models.Index(fields=['Expiry_Date'], name='idx_products_expiry'),
        ]
    
    def __str__(self):
        return self.Name


class Item_Movements(models.Model):
    ID = models.AutoField(primary_key=True, db_column='ID')
    Products_in_storage_ID = models.ForeignKey(
        'Products_in_storage', 
        on_delete=models.CASCADE, 
        db_column='Products_in_storage_ID',
        to_field='ID'
    )
    Action = models.CharField(
        max_length=10,
        choices=[('disposal', 'disposal'), ('adding', 'adding')],
        db_column='Action'
    )
    Date_Time = models.DateTimeField(auto_now_add=True, db_column='Date_Time')
    Description = models.TextField(null=True)
    Employee_ID = models.ForeignKey(
        'Employees', 
        on_delete=models.RESTRICT, 
        db_column='Employee_ID',
        to_field='ID',
        
    )
    
    class Meta:
        
        db_table = 'Item_Movements'
        indexes = [
            models.Index(fields=['Date_Time'], name='idx_movements_date'),
            models.Index(fields=['Products_in_storage_ID'], name='idx_movements_product'),
        ]
    
    def __str__(self):
        return f"{self.Action} - {self.Quantity}"