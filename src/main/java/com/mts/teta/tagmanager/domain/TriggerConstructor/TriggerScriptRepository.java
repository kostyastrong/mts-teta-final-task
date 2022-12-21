package com.mts.teta.tagmanager.domain.TriggerConstructor;

public class TriggerScriptRepository {
    /*
     * SimpleTemplate предназначен для простых скриптов, отслеживающих конкретное событие или элемент, к которому
     * можно получить доступ за одно действие. Например, document.addEventListener('click', function(){}) или
     * setInterval(function(){}, delayMillis).
     *
     * Шаблон имеет следующую структуру:
     * (function() {
     *   {beforePrimaryFunction}
     *   {primaryFunction} ({beforeTriggerAttributes} function(eventObject) {
     *     // post request to given api
     *   } {afterTriggerAttributes})
     *   {afterPrimaryFunction}
     * })()
     *
     * Обязательными для замены в шаблоне являются параметры:
     * {primaryFunction} - в формате something.addListener
     * {triggerName} - имя триггера
     * {requestUrl} - адрес, по которому нужно обращаться к API
     * {userId} - ID пользователя, активировавшего триггер
     * {elementName} - уникальное имя (ID) объекта, к которому привязан активированный триггер. В случае, если триггер
     *                  прикреплён к глобальным событиям на странице, стоит заменить это поле на null
     * {appId} - ID приложения, в котором произошёл триггер
     * {eventAttributes} - прочие атрибуты произошедшего триггера. В случае их отсутствия необходимо заменить это поле
     *                     на null
     *
     * Полный список задействованных замен:
     * {triggerName}
     * {appId}
     * {appName}
     * {userId}
     * {elementName}
     * {triggerName}
     * {requestUrl}
     * {eventAttributes}
     * {primaryFunction}
     * {beforePrimaryFunction}
     * {afterPrimaryFunction}
     * {beforeTriggerAttributes}
     * {afterTriggerAttributes}
     */

    public static String SimpleTemplate = """
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
              {beforePrimaryFunction}
              console.log("Trigger {triggerName} is activated");
              {primaryFunction}({beforeTriggerAttributes} function(eventObject) {
                  console.log("Trigger {triggerName} is performing the action");
                  // здесь отправляется сообщение на бэкенд
                  fetch('{requestUrl}', {
                      method: 'POST',
                      mode: 'no-cors',
                      headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                      },
                      // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
                      body: JSON.stringify({
                          "userId": getCookie("userId"),
                          "event": "{triggerName}",
                          "element": {elementName}, // setInterval не привязан к какому-то конкретному элементу на странице
                          // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                          "appName": "{appName}",
                          "appId": "{appId}",
                          // в eventParams как раз сохраняет trigger.attributes
                          "eventParams": {eventAttributes}
                      })
                  })
              } {afterTriggerAttributes})
              {afterPrimaryFunction}
            })()
            """;
    /*
     * MultipleElementsTemplate предназначен для добавления триггеров нескольким элементам одного типа.
     * Это могут быть кнопки, поля ввода, чекбоксы и прочие объекты.
     *
     * Шаблон имеет следующую структуру:
     * (function() {
     *   // var elements = {elementsSet}
     *   for (element : elements) {
     *     {beforePrimaryFunction}
     *     {primaryFunction} ({beforeTriggerAttributes} function(eventObject) {
     *       // post request to given api
     *     } {afterTriggerAttributes})
     *     {afterPrimaryFunction}
     *  }
     * })()
     *
     * Обязательными для замены в шаблоне являются параметры:
     * {elementsSet} - контейнер содержащий все требуемые элементы. Например, document.querySelectorAll("button")
     * {primaryFunction} - в формате something.addListener
     * {triggerName} - имя триггера
     * {requestUrl} - адрес, по которому нужно обращаться к API
     * {userId} - ID пользователя, активировавшего триггер
     * {elementName} - уникальное имя (ID) объекта, к которому привязан активированный триггер. В случае, если триггер
     *                  прикреплён к глобальным событиям на странице, стоит заменить это поле на null
     * {appId} - ID приложения, в котором произошёл триггер
     * {eventAttributes} - прочие атрибуты произошедшего триггера. В случае их отсутствия необходимо заменить это поле
     *                     на null
     *
     * Полный список задействованных замен:
     * {elementsSet}
     * {triggerName}
     * {appId}
     * {appName}
     * {userId}
     * {elementName}
     * {triggerName}
     * {requestUrl}
     * {eventAttributes}
     * {primaryFunction}
     * {beforePrimaryFunction}
     * {afterPrimaryFunction}
     * {beforeTriggerAttributes}
     * {afterTriggerAttributes}
     */
    public static String MultipleElementsTemplate = """
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
              
              var elements = {elementsSet};
              console.log("Trigger {triggerName} is activated");
              for (var i = 0, len = elements.length; i < len; i++) {
                {primaryFunction}({beforeTriggerAttributes} function(eventObject) {
                  {beforePrimaryFunction}
                  console.log("Trigger {triggerName} is performing the action");
                  // здесь отправляется сообщение на бэкенд
                  fetch('{requestUrl}', {
                    method: 'POST',
                    mode: 'no-cors',
                    headers: {
                      'Accept': 'application/json',
                      'Content-Type': 'application/json'
                    },
                    // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
                    body: JSON.stringify({
                      "userId": getCookie("userId"),
                      "event": "{triggerName}",
                      "element": {elementName}, // setInterval не привязан к какому-то конкретному элементу на странице
                      // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
                      "appName": "{appName}",
                      "appId": "{appId}",
                      // в event_params как раз сохраняет trigger.attributes
                      "eventParams": {eventAttributes}
                    })
                   })
                  {afterPrimaryFunction}
                } {afterTriggerAttributes})
              }
            })()
            """;
}
