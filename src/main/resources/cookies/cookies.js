// дополнительно оборачивание в function - хак, который позволяет
// выполнить код сразу при загрузке страницы
(function() {
    function setCookie(cname, cvalue) {
        const d = new Date();
        d.setTime(d.getTime() + (10*24*60*60*1000));  // 10
        let expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    function getCookie(cname) {
        let name = cname + "=";
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for(let i = 0; i <ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        //let num = Math.floor(Math.random()).toString();
        let num = '2';
        setCookie("userId", num);
        return num.toString();
    }

    console.log("Trigger SCROLL is activated");
    document.addEventListener('scroll',  function(eventObject) {
        console.log("Trigger SCROLL is performing the action");
        // здесь отправляется сообщение на бэкенд
        fetch('http://localhost:8080/api/message', {
            method: 'POST',
            mode: 'no-cors',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
            body: JSON.stringify({
                "userId": getCookie("userId"),
                "event": "SCROLL",
                "element": null, // setInterval не привязан к какому-то конкретному элементу на странице
                // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                "appName": "abacaba",
                "appId": "1",
                // в eventParams как раз сохраняет trigger.attributes
                "eventParams": {"additionalProp1":{},"additionalProp2":{},"additionalProp3":{}}
            })
        })
    } )

})()
;
// дополнительно оборачивание в function - хак, который позволяет
// выполнить код сразу при загрузке страницы
(function() {
    function setCookie(cname, cvalue) {
        const d = new Date();
        d.setTime(d.getTime() + (10*24*60*60*1000));  // 10
        let expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    function getCookie(cname) {
        let name = cname + "=";
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for(let i = 0; i <ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        //let num = Math.floor(Math.random()).toString();
        let num = '2';
        setCookie("userId", num);
        return num.toString();
    }

    console.log("Trigger CLICK is activated");
    document.addEventListener('click',  function(eventObject) {
        console.log("Trigger CLICK is performing the action");
        // здесь отправляется сообщение на бэкенд
        fetch('http://localhost:8080/api/message', {
            method: 'POST',
            mode: 'no-cors',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
            body: JSON.stringify({
                "userId": getCookie("userId"),
                "event": "CLICK",
                "element": null, // setInterval не привязан к какому-то конкретному элементу на странице
                // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                "appName": "abacaba",
                "appId": "1",
                // в eventParams как раз сохраняет trigger.attributes
                "eventParams": {"additionalProp1":{},"additionalProp2":{},"additionalProp3":{}}
            })
        })
    } )

})()
;
// дополнительно оборачивание в function - хак, который позволяет
// выполнить код сразу при загрузке страницы
(function() {
    function setCookie(cname, cvalue) {
        const d = new Date();
        d.setTime(d.getTime() + (10*24*60*60*1000));  // 10
        let expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    function getCookie(cname) {
        let name = cname + "=";
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for(let i = 0; i <ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        //let num = Math.floor(Math.random()).toString();
        let num = '2';
        setCookie("userId", num);
        return num.toString();
    }

    var elements = document.querySelectorAll("button");
    console.log("Trigger BUTTON_CLICK is activated");
    for (var i = 0, len = elements.length; i < len; i++) {
        elements[i].addEventListener('click',  function(eventObject) {

            console.log("Trigger BUTTON_CLICK is performing the action");
            // здесь отправляется сообщение на бэкенд
            fetch('http://localhost:8080/api/message', {
                method: 'POST',
                mode: 'no-cors',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
                body: JSON.stringify({
                    "userId": getCookie("userId"),
                    "event": "BUTTON_CLICK",
                    "element": eventObject.target.className, // setInterval не привязан к какому-то конкретному элементу на странице
                    // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                    "appName": "abacaba",
                    "appId": "1",
                    // в event_params как раз сохраняет trigger.attributes
                    "eventParams": {"additionalProp1":{},"additionalProp2":{},"additionalProp3":{}}
                })
            })

        } )
    }
})()
;
// дополнительно оборачивание в function - хак, который позволяет
// выполнить код сразу при загрузке страницы
(function() {
    function setCookie(cname, cvalue) {
        const d = new Date();
        d.setTime(d.getTime() + (10*24*60*60*1000));  // 10
        let expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    function getCookie(cname) {
        let name = cname + "=";
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for(let i = 0; i <ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        //let num = Math.floor(Math.random()).toString();
        let num = '2';
        setCookie("userId", num);
        return num.toString();
    }

    var elements = document.querySelectorAll("input[type=text]");
    console.log("Trigger FOCUS_IN is activated");
    for (var i = 0, len = elements.length; i < len; i++) {
        elements[i].addEventListener('focusin',  function(eventObject) {

            console.log("Trigger FOCUS_IN is performing the action");
            // здесь отправляется сообщение на бэкенд
            fetch('http://localhost:8080/api/message', {
                method: 'POST',
                mode: 'no-cors',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
                body: JSON.stringify({
                    "userId": getCookie("userId"),
                    "event": "FOCUS_IN",
                    "element": eventObject.target.className, // setInterval не привязан к какому-то конкретному элементу на странице
                    // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                    "appName": "abacaba",
                    "appId": "1",
                    // в event_params как раз сохраняет trigger.attributes
                    "eventParams": {"additionalProp1":{},"additionalProp2":{},"additionalProp3":{}}
                })
            })

        } )
    }
})()
;
// дополнительно оборачивание в function - хак, который позволяет
// выполнить код сразу при загрузке страницы
(function() {
    function setCookie(cname, cvalue) {
        const d = new Date();
        d.setTime(d.getTime() + (10*24*60*60*1000));  // 10
        let expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    function getCookie(cname) {
        let name = cname + "=";
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for(let i = 0; i <ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        //let num = Math.floor(Math.random()).toString();
        let num = '2';
        setCookie("userId", num);
        return num.toString();
    }

    var elements = document.querySelectorAll("input[type=text]");
    console.log("Trigger FOCUS_OUT is activated");
    for (var i = 0, len = elements.length; i < len; i++) {
        elements[i].addEventListener('focusout',  function(eventObject) {

            console.log("Trigger FOCUS_OUT is performing the action");
            // здесь отправляется сообщение на бэкенд
            fetch('http://localhost:8080/api/message', {
                method: 'POST',
                mode: 'no-cors',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
                body: JSON.stringify({
                    "userId": getCookie("userId"),
                    "event": "FOCUS_OUT",
                    "element": eventObject.target.className, // setInterval не привязан к какому-то конкретному элементу на странице
                    // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                    "appName": "abacaba",
                    "appId": "1",
                    // в event_params как раз сохраняет trigger.attributes
                    "eventParams": {"additionalProp1":{},"additionalProp2":{},"additionalProp3":{}}
                })
            })

        } )
    }
})()
;
// дополнительно оборачивание в function - хак, который позволяет
// выполнить код сразу при загрузке страницы
(function() {
    function setCookie(cname, cvalue) {
        const d = new Date();
        d.setTime(d.getTime() + (10*24*60*60*1000));  // 10
        let expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    function getCookie(cname) {
        let name = cname + "=";
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for(let i = 0; i <ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        let num = Math.floor(Math.random() % 4 + 1).toString();
        // let num = '2';
        setCookie("userId", num);
        return num.toString();
    }

    console.log("Trigger SET_INTERVAL is activated");
    setInterval( function(eventObject) {
        console.log("Trigger SET_INTERVAL is performing the action");
        let userId = getCookie("userId");
        // здесь отправляется сообщение на бэкенд
        fetch('http://localhost:8080/api/message', {
            method: 'POST',
            mode: 'no-cors',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
            body: JSON.stringify({
                "userId": userId,
                "event": "SET_INTERVAL",
                "element": null, // setInterval не привязан к какому-то конкретному элементу на странице
                // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                "appName": "abacaba",
                "appId": "1",
                // в eventParams как раз сохраняет trigger.attributes
                "eventParams": {"additionalProp1":{},"additionalProp2":{},"additionalProp3":{}}
            })
        })
    } , 10000)

})()
