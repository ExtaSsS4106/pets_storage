class Login_page {
    get_data() {
        const email = document.getElementById('email')?.value || '';
        const password = document.getElementById('password')?.value || '';
        
        return { email, password };
    }

    async init_page() {
        const btn = document.getElementById('login-btn');
        btn.addEventListener('click', async () => {
            const data = this.get_data();
            try {
                const response = await eel.login(data.email, data.password)();
                console.error(response);
                if (response.status === "200") {
                    loadPage("main");
                }
            } catch (error) {
                console.error("Ошибка входа:", error);
            }
        });
    }
}