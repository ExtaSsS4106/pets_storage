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
    path('change_status', views.change_status),
    path('select_all', views.select_all),
    path('select_movment', views.select_movment),
    path('select_employees', views.select_employees),
    path('delete_employees', views.delete_employees),
    path('select_expired', views.select_expired),
    path('select_deleted', views.select_deleted),
    path('select_active_with_ids', views.select_active_with_ids),
    path('select_delivery_with_ids', views.select_delivery_with_ids),
    path('select_movment/<str:action>', views.select_movment_by_action),
    path('select_employees/<int:position_id>', views.select_employees_by_position),
]