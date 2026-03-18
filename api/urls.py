from django.contrib import admin
from django.urls import path, re_path
from .import views 
from django.conf.urls.static import static
from django.conf import settings

urlpatterns = [
    path('login', views.login_api),
    path('sign_up', views.sign_up_api),
    path('logout', views.logout_api),
    path('add', views.add),
    path('delete', views.delete),
    path('select_all', views.select_all),
    path('select_movment', views.select_movment),
    path('select_employees', views.select_employees),
    path('delete_employees', views.delete_employees),
]