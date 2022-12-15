// дополнительно оборачивание в function - хак, который позволяет
// выполнить код сразу при загрузке страницы
(function() {
  console.log("Trigger {triggerName} is activated");
  /*
    В данном случае, никаких дополнительных листенеров не нужно, потому что мы просто регистрируем функцию
    через setInterval, которая периодически выполняется.
    Если же вы, например, захотите отслеживать события клика, или скролла, то вам нужно будет добавить
    соответствующие слушатели:

    document.addEventListener('click', function() {...});
    document.addEventListener('scroll', function() {...});

    и так далее
  */
  setInterval(function() {
      console.log("Trigger {triggerName} is performing the action");
      // здесь отправляется сообщение на бэкенд
      // Endpoint, как видите, захардкожен. При дефолтных настройках все будет работать.
      // Но лучше, если это поле будет конфигурируемым
      fetch('{requestUrl}', {
          method: 'POST',
          mode: 'no-cors',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          // к trigger.attributes прибавляем еще кастомные атрибуты: userId, event, element, app
          body: JSON.stringify({
              "userId": "{userId}",
              "event": "set_interval",
              "element": null, // setInterval не привязан к какому-то конкретному элементу на странице
              // информация о приложении нужна, чтобы мы понимали, к кому относится данное событие
              "app_name": "{appName}",
              "app_id": {appId},
              // в event_params как раз сохраняет trigger.attributes
              "event_params": {attributes}
          })
      })
  }, {delayMillis})
})()