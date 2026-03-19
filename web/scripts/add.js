class Add_page {
    async get_data(value, param = null) {
        // Здесь будут запросы к API если нужно
    }

    collectFormData() {
        // Собираем данные из формы
        const formData = {
            supplier: document.querySelector('.form-group input[placeholder*="Поставщик"]')?.value || '',
            delivery_date: document.querySelector('.form-group input[type="date"]')?.value || '',
            responsible: document.querySelector('.form-group input[readonly]')?.value || '',
            comment: document.querySelector('.form-group textarea')?.value || '',
            products: []
        };

        // Собираем все товары
        document.querySelectorAll('.product-row').forEach(row => {
            const inputs = row.querySelectorAll('input');
            const selects = row.querySelectorAll('select');
            
            const product = {
                name: inputs[0]?.value || '',
                category_id: selects[0]?.value || '1',
                animal_type_id: selects[1]?.value || '1',
                expiry_date: inputs[1]?.value || '',
                quantity: parseInt(inputs[2]?.value) || 1,
                status: 'delivery'
            };
            
            // Добавляем только если заполнено название
            if (product.name) {
                formData.products.push(product);
            }
        });

        return formData;
    }

    generateJSON() {
        const formData = this.collectFormData();
        
        // Генерируем JSON для отправки на сервер
        // Каждая единица товара будет отдельной записью
        const newProducts = [];
        
        formData.products.forEach(product => {
            for (let i = 0; i < product.quantity; i++) {
                newProducts.push({
                    name: product.name,
                    manufacturer: formData.supplier, // Используем поставщика как производителя
                    category_id: parseInt(product.category_id),
                    animal_type_id: parseInt(product.animal_type_id),
                    expiry_date: product.expiry_date,
                    delivery_date: formData.delivery_date || new Date().toISOString().split('T')[0],
                    status: product.status
                });
            }
        });

        const jsonData = {
            new_products: newProducts
        };

        console.log('Сгенерированный JSON:', JSON.stringify(jsonData, null, 2));
        return jsonData;
    }

    updatePreview() {
        const formData = this.collectFormData();
        
        // Обновляем информацию в предпросмотре
        const infoCards = document.querySelectorAll('.info-card .value');
        if (infoCards.length >= 4) {
            infoCards[0].textContent = formData.supplier || '-'; // Поставщик
            infoCards[1].textContent = formData.delivery_date ? new Date(formData.delivery_date).toLocaleDateString('ru-RU') : '-'; // Дата
        }
        
        // Обновляем номер накладной в заголовке предпросмотра
        const previewTitle = document.querySelector('.delivery-info span:first-child');
        if (previewTitle) {
            previewTitle.textContent = formData.invoice_number ? `Поставка #${formData.invoice_number}` : 'Поставка #';
        }
        
        // Обновляем количество позиций и единиц
        const totalItems = formData.products.length;
        const totalQuantity = formData.products.reduce((sum, p) => sum + p.quantity, 0);
        
        if (infoCards.length >= 4) {
            infoCards[2].textContent = totalItems || '-'; // Всего позиций
            infoCards[3].textContent = totalQuantity ? totalQuantity + ' шт' : '-'; // Всего единиц
        }
        
        // Обновляем итоги в summary
        const summaryItems = document.querySelectorAll('.summary-item span:last-child');
        if (summaryItems.length >= 4) {
            summaryItems[0].textContent = totalItems || '-';
            summaryItems[1].textContent = totalQuantity ? totalQuantity + ' шт' : '-';
            summaryItems[2].textContent = totalQuantity || '-';
            summaryItems[3].textContent = totalQuantity ? totalQuantity + ' шт' : '-';
        }

        // Обновляем или создаем карточки товаров в предпросмотре
        const productCardsContainer = document.querySelector('.product-cards');
        productCardsContainer.innerHTML = ''; // Очищаем
        
        formData.products.forEach(product => {
            if (!product.name) return;
            
            const card = document.createElement('div');
            card.className = 'product-card';
            card.innerHTML = `
                <div class="product-header">
                    <span class="product-name">${product.name}</span>
                    <span class="product-quantity">${product.quantity} шт</span>
                </div>
                <div class="product-details">
                    <div class="product-detail">🏭 ${formData.supplier || 'Не указан'}</div>
                    <div class="product-detail">📦 ${product.category_id == 1 ? 'сухой' : 'жидкий'}</div>
                    <div class="product-detail">${product.animal_type_id == 1 ? '🐶 собачий' : '🐱 кошачий'}</div>
                    <div class="product-detail">📅 ${product.expiry_date ? new Date(product.expiry_date).toLocaleDateString('ru-RU') : 'Не указан'}</div>
                </div>
                <div style="margin-top: 10px; font-size: 12px; color: #64748b;">
                    ⚡ Будет разбито на ${product.quantity} отдельных записей в БД
                </div>
            `;
            productCardsContainer.appendChild(card);
        });
    }

    async init_page() {
        // Функционал для добавления/удаления товаров
        document.querySelectorAll('.remove-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                this.removeProduct(btn);
            });
        });

        document.querySelector('.add-product-btn').addEventListener('click', () => {
            this.addProductRow();
        });

        // Делегирование событий для динамически добавляемых элементов
        document.querySelector('.products-table').addEventListener('click', (e) => {
            if (e.target.classList.contains('remove-btn')) {
                this.removeProduct(e.target);
            }
        });

        // Обновление предпросмотра при изменении полей
        const updateFields = document.querySelectorAll(
            '.product-input, .quantity-input, ' +
            '.form-group input[placeholder*="Поставщик"], ' +
            '.form-group input[type="date"], ' +
            '.form-group input[placeholder*="Накладной"], ' +
            '.form-group textarea, ' +
            '.form-group select'
        );
        
        updateFields.forEach(input => {
            input.addEventListener('input', () => {
                this.updatePreview();
            });
            input.addEventListener('change', () => {
                this.updatePreview();
            });
        });

        // Кнопка сохранения
        document.querySelector('.btn-primary')?.addEventListener('click', async () => {
            const jsonData = this.generateJSON();
            
            // Здесь будет отправка на сервер
            console.log('Отправка данных на сервер:', jsonData);
            
            await eel.add_products(jsonData)();
            
        });

        // Кнопка отмены
        document.querySelector('.btn-secondary')?.addEventListener('click', () => {
            if (confirm('Отменить добавление поставки?')) {
                // Очищаем форму или возвращаемся назад
               
            }
        });

        // Первоначальное обновление предпросмотра
        this.updatePreview();
    }

    addProductRow() {
        const productsTable = document.querySelector('.products-table');
        const newRow = document.createElement('div');
        newRow.className = 'product-row';
        newRow.innerHTML = `
            <input type="text" class="product-input" placeholder="Название товара">
            <select class="product-input">
                <option value="1">сухой</option>
                <option value="2">жидкий</option>
            </select>
            <select class="product-input">
                <option value="1">собачий</option>
                <option value="2">кошачий</option>
            </select>
            <input type="date" class="product-input" placeholder="Срок годности">
            <input type="number" class="quantity-input" value="1" min="1" max="999">
            <button class="remove-btn">✕</button>
        `;
        productsTable.appendChild(newRow);
        
        // Добавляем обработчики для новых полей
        newRow.querySelectorAll('.product-input, .quantity-input').forEach(input => {
            input.addEventListener('input', () => this.updatePreview());
            input.addEventListener('change', () => this.updatePreview());
        });

        // Обновляем предпросмотр
        this.updatePreview();
    }

    removeProduct(btn) {
        const row = btn.closest('.product-row');
        if (row) {
            row.remove();
            this.updatePreview();
        }
    }
}