function f(id) {
    var b1 = document.getElementById(id);
    b1.removeAttribute("class")
    b1.setAttribute("class", "btn btn-success mt-3");
}

function enter() {
    let username = document.getElementById("username");
    let password = document.getElementById("password");

    if(username === ""){
        alert("Введите имя пользователя");
        return false;
    }
    if(password === ""){
        alert("Введите пароль");
        return false;
    }
    return true;
}