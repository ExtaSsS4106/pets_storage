class Main_page {
    async get_data(value, param = null) {
        let data;
        switch(value) {
        case "select_products":
            data = await eel.select_products()();
            return data;
        case "select_movment":
            data = await eel.select_movment()();
            return data;
        case "select_employees":
            data = await eel.select_employees()();
            return data;
        case "select_expired":
            data = await eel.select_expired()();
            return data;
        case "select_deleted":
            data = await eel.select_deleted()();
            return data;
        case "select_movment_by_action":
            data = await eel.select_movment_by_action(param)();
            return data;
        case "select_employees_by_position":
            data = await eel.select_employees_by_position(param)();
            return data;
        default:
            console.log("nothing to show");
            break;
        }
    }

    async init_page() {
        const storage = document.getElementById('storage');
        const movment = document.getElementById('movment');
        const employees = document.getElementById('employees');
        const add_delivery = document.getElementById('add_delivery'); 
        const disposal = document.getElementById('disposal');

        disposal.addEventListener('click', async () => {
            loadPage('disposal');
        });

        add_delivery.addEventListener('click', async () => {
            loadPage('add_page');
        });

        storage.addEventListener('click', async () => {
            const data = await this.get_data('select_products');
            this.insert_products(data);
        });
        
        movment.addEventListener('click', async () => {
            const data = await this.get_data('select_movment');
            this.insert_movment(data);
        });
        
        employees.addEventListener('click', async () => {
            const data = await this.get_data('select_employees');
            this.insert_employees(data);
        });
    }

    async insert_products(data){
        const items = document.getElementById('items');
        const head = document.querySelector('.head h3');
        head.textContent = '📦 Склад';
        
        const control = document.querySelector('.control');
        control.innerHTML = `
            <button id="all-products" class="control-btn"><h3>Все</h3></button>
            <button id="expired-products" class="control-btn"><h3>Срочки</h3></button>
            <button id="deleted-products" class="control-btn"><h3>Списанные</h3></button>
        `;
        
        document.getElementById('all-products').addEventListener('click', async () => {
            const data = await this.get_data('select_products');
            this.insert_products(data);
        });
        
        document.getElementById('expired-products').addEventListener('click', async () => {
            const data = await this.get_data('select_expired');
            this.insert_products(data);
        });
        
        document.getElementById('deleted-products').addEventListener('click', async () => {
            const data = await this.get_data('select_deleted');
            this.insert_products(data);
        }); 
        
        // Проверяем наличие ошибки
        if (data && data.error) {
            items.innerHTML = `<div class="error-message">${data.message}</div>`;
            return;
        }
        
        // Проверяем на пустые данные
        if (data === 'empty' || !data || data.length === 0) {
            items.innerHTML = '<div class="empty-message">Нет товаров</div>';
            return;
        }
        
        const cardsHtml = data.map(element => `
            <div class="card">
                <div class="card-ico"><h1>📦</h1></div>
                <div class="card-info">
                    <div class="card-info-top">
                        <div class="info-fillds">${element.name || 'Без названия'}</div>
                        <div class="info-fillds">${element.manufacturer || 'Не указан'}</div>
                    </div>
                    <div class="card-info-bottom">
                        <div class="info-fillds">${element.total_quantity} шт.</div>
                        <div class="info-fillds">до: ${element.expiry_date || 'Не указан'}</div>
                        <div class="info-fillds">${element.animal_type || 'Не указан'}</div>
                        <div class="info-fillds">${element.category || 'Не указан'}</div>
                        <div class="info-fillds">${element.status || 'Не указан'}</div>
                    </div>
                </div>
                <div class="card-btns"></div>
            </div>
        `).join('');
        
        items.innerHTML = cardsHtml;
    }

    async insert_movment(data){
        const items = document.getElementById('items');
        const head = document.querySelector('.head h3');
        head.textContent = '📊 Движения товаров';
        
        const control = document.querySelector('.control');
        control.innerHTML = `
            <button id="all-movments" class="control-btn"><h3>Все</h3></button>
            <button id="adding-movments" class="control-btn"><h3>Добавления</h3></button>
            <button id="disposal-movments" class="control-btn"><h3>Списания</h3></button>
        `;
        
        document.getElementById('all-movments').addEventListener('click', async () => {
            const data = await this.get_data('select_movment');
            this.insert_movment(data);
        });
        
        document.getElementById('adding-movments').addEventListener('click', async () => {
            const data = await this.get_data('select_movment_by_action', 'adding');
            this.insert_movment(data);
        });
        
        document.getElementById('disposal-movments').addEventListener('click', async () => {
            const data = await this.get_data('select_movment_by_action', 'disposal');
            this.insert_movment(data);
        });
        
        // Проверяем наличие ошибки
        if (data && data.error) {
            items.innerHTML = `<div class="error-message">${data.message}</div>`;
            return;
        }
        
        // Проверяем на пустые данные
        if (data === 'empty' || !data || data.length === 0) {
            items.innerHTML = '<div class="empty-message">Нет движений</div>';
            return;
        }
        
        const cardsHtml = data.map(element => {
            let actionIcon = '';
            
            switch(element.action?.toLowerCase()) {
                case 'adding':
                    actionIcon = '➕';
                    break;
                case 'disposal':
                    actionIcon = '🗑️';
                    break;
                default:
                    actionIcon = '📝';
            }
            
            return  `
                <div class="card">
                    <div class="card-ico"><h1>${actionIcon}</h1></div>
                    <div class="card-info">
                        <div class="card-info-top">
                            <div class="info-fillds">${element.name || 'Без названия'}</div>
                            <div class="info-fillds">${element.action || 'Не указан'}</div>
                            <div class="info-fillds">${element.manufacturer || 'Не указан'}</div>
                        </div>
                        <div class="card-info-bottom">
                            <div class="info-fillds">Когда: ${element.date_time || 'Не указан'}</div>
                            <div class="info-fillds">Кем: ${element.employee || 'Не указан'}</div>
                        </div>
                    </div>
                    <div class="card-btns"></div>
                </div>
            `;
        }).join('');
        
        items.innerHTML = cardsHtml;
    }

    async insert_employees(data){
        const items = document.getElementById('items');
        const head = document.querySelector('.head h3');
        head.textContent = '👥 Сотрудники';
        
        const control = document.querySelector('.control');
        control.innerHTML = `
            <button id="all-employees" class="control-btn"><h3>Все</h3></button>
            <button id="admins" class="control-btn"><h3>Админы</h3></button>
            <button id="storekeepers" class="control-btn"><h3>Кладовщики</h3></button>
        `;
        
        document.getElementById('all-employees').addEventListener('click', async () => {
            const data = await this.get_data('select_employees');
            this.insert_employees(data);
        });
        
        document.getElementById('admins').addEventListener('click', async () => {
            const data = await this.get_data('select_employees_by_position', 1);
            this.insert_employees(data);
        });
        
        document.getElementById('storekeepers').addEventListener('click', async () => {
            const data = await this.get_data('select_employees_by_position', 2);
            this.insert_employees(data);
        });
        
        // Проверяем наличие ошибки
        if (data && data.error) {
            items.innerHTML = `<div class="error-message">${data.message}</div>`;
            return;
        }
        
        // Проверяем на пустые данные
        if (data === 'empty' || !data || data.length === 0) {
            items.innerHTML = '<div class="empty-message">Нет сотрудников</div>';
            return;
        }
        
        const cardsHtml = data.map(element =>  
            `
                <div class="card">
                    <div class="card-ico"><h1>👥</h1></div>
                    <div class="card-info">
                        <div class="card-info-top">
                            <div class="info-fillds">${element.full_name || 'Без названия'}</div>
                            <div class="info-fillds">${element.position || 'Не указан'}</div>
                        </div>
                        <div class="card-info-bottom">
                            <div class="info-fillds">${element.date_of_birth || 'Не указан'}</div>
                            <div class="info-fillds">Тел: ${element.phone_number || 'Не указан'}</div>
                            <div class="info-fillds">Почта: ${element.email || 'Не указан'}</div>
                        </div>
                    </div>
                    <div class="card-btns"></div>
                </div>
            `
        ).join('');
        
        items.innerHTML = cardsHtml;
    }
}