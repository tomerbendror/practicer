<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>Quiz App</title>

    <meta name="viewport" content="width=device-width, minimum-scale=1.0, initial-scale=1, user-scalable=yes">
    <meta name="mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style"
          content="black-translucent" >
    <meta name="format-detection" content="telephone=no">

    <link rel="shortcut icon" href="favicon.ico">
    <link rel="icon" sizes="196x196" href="icons/icon-196.png">
    <link rel="apple-touch-icon" sizes="152x152" href="icons/iPad@2x.png">
    <link rel="apple-touch-icon" sizes="144x144" href="icons/Icon-72@2x.png">
    <link rel="apple-touch-icon" sizes="120x120" href="icons/iPhone@2x.png">

    <meta name="application-name" content="Quiz App">

    <!--
    Chrome 35 has old :host behavior before :host-context addition.
    Since Chrome 35 does not have HTMLImports, we can use this as a decent signal
    to force the ShadowDOM Polyfill.
    -->
    <script>
        if (!('import' in document.createElement('link'))) {
            var Platform = {flags: {shadow: true}};
        }
    </script>

    <script src="/childApp/components/webcomponentsjs/webcomponents.js"></script>

    <link rel="stylesheet" href="/childApp/theme.css" shim-shadowdom>

    <!--
    <link rel="import" href="components/font-roboto/roboto.html">
    -->
    <link rel="import" href="/childApp/polyfills/fonts/roboto.html">
    <link rel="import" href="/childApp/components/core-icons/core-icons.html">
    <link rel="import" href="/childApp/components/core-icons/av-icons.html">
    <link rel="import" href="/childApp/components/core-icons/social-icons.html">
    <link rel="import" href="/childApp/components/topeka-elements/topeka-datasource.html">
    <link rel="import" href="/childApp/components/topeka-elements/topeka-app.html">
    <link rel="import" href="/childApp/components/topeka-elements/theme.html">

    <script>
        if (navigator.serviceWorker) {
            // Register our ServiceWorker
            navigator.serviceWorker.register("/childApp/sw.js");
        }
    </script>

</head>
<body>

<template is="auto-binding">
    <topeka-datasource url="/childApp/components/topeka-elements/categories.json" user="{{user}}" categories="{{categories}}" connected="{{connected}}"></topeka-datasource>
    <topeka-app fit user="{{user}}" categories="{{categories}}" connected="{{connected}}" touch-action="auto"></topeka-app>
</template>

</body>
</html>
