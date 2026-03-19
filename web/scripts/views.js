console.log("views.js inited")



async function loadPage(value) {
    let content;
    const link = document.querySelector('link[rel="stylesheet"]');
    switch(value) {
        case "login":
            let loginPage = new Login_page();
            content = await eel.login()();
            document.querySelector('body').innerHTML = content;
            link.href = `../static/login.css`;
            loginPage.init_page();
            break;
        case "register":
            let RegPage = new Register_page();
            content = await eel.sign_up()();
            document.querySelector('body').innerHTML = content;
            RegPage.init_page();
            break;
        case "main":
            [content, data] = await eel.main()(); 
            document.querySelector('body').innerHTML = content;
            link.href = `../static/main.css`;
            let MainPage = new Main_page();
            MainPage.init_page();
            await eel.set_load_page('main')(); 
            break;
        case "add_page":
            [content, data] = await eel.add_page()(); 
            document.querySelector('body').innerHTML = content;
            link.href = `../static/add.css`;
            let AddPage = new Add_page();
            AddPage.init_page();
            await eel.set_load_page('add_page')(); 
            break;
        default:
            console.log("nothing to show");
            document.querySelector('body').innerHTML = '<h1>Такой страницы не существует</h1>';
            break;
    }
}