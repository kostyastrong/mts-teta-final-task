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
    $(document).click(function() {
        console.log("Trigger click is performing the action");
        // здесь отправляется сообщение на бэкенд
        // Endpoint, как видите, захардкожен. При дефолтных настройках все будет работать.
        // Но лучше, если это поле будет конфигурируемым

    })
})
})()