package ru.javaboys.defidog.util;

public class MailTemplates {
    private static final String CODE = """
            <!DOCTYPE html>
            <html lang="ru">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Код подтверждения - Defidog</title>
                <style>
                    body {
                        background-color: #f4f4f4;
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        background-color: #ffffff;
                        max-width: 600px;
                        margin: 50px auto;
                        border-radius: 10px;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                    }
                    .header {
                        background-color: #e9c984;
                        color: white;
                        text-align: center;
                        padding: 20px;
                    }
                    .content {
                        padding: 30px;
                        text-align: center;
                    }
                    .code {
                        display: inline-block;
                        background-color: #eeeeee;
                        color: #333333;
                        font-size: 32px;
                        font-weight: bold;
                        letter-spacing: 8px;
                        padding: 15px 25px;
                        border-radius: 8px;
                        margin: 20px 0;
                    }
                    .footer {
                        text-align: center;
                        font-size: 12px;
                        color: #888888;
                        padding: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Defidog</h1>
                    </div>
                    <div class="content">
                        <h2>Подтверждение электронной почты</h2>
                        <p>Пожалуйста, используйте следующий код для подтверждения вашего адреса электронной почты:</p>
                        <div class="code">{{CODE}}</div>
                        <p>Если вы не запрашивали этот код, просто проигнорируйте это письмо.</p>
                    </div>
                    <div class="footer">
                        &copy; 2025 Defidog. Все права защищены.
                    </div>
                </div>
            </body>
            </html>
            """;
    public static String forCode(String code) {
        return CODE.replace("{{CODE}}", code);
    }

    private static final String NOTIFICATION = """
            <!DOCTYPE html>
            <html lang="ru">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Отчет безопасности - Defidog</title>
                <style>
                    body {
                        background-color: #f4f4f4;
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        background-color: #ffffff;
                        max-width: 600px;
                        margin: 50px auto;
                        border-radius: 10px;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                    }
                    .header {
                        background-color: #e9c984;
                        color: white;
                        text-align: center;
                        padding: 20px;
                    }
                    .content {
                        padding: 30px;
                        text-align: center;
                    }
                    .code {
                        display: inline-block;
                        background-color: #eeeeee;
                        color: #333333;
                        font-size: 32px;
                        font-weight: bold;
                        letter-spacing: 8px;
                        padding: 15px 25px;
                        border-radius: 8px;
                        margin: 20px 0;
                    }
                    .footer {
                        text-align: center;
                        font-size: 12px;
                        color: #888888;
                        padding: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Defidog</h1>
                    </div>
                    <div class="content">
                        {{NOTIFICATION}}
                    </div>
                    <div class="footer">
                        &copy; 2025 Defidog. Все права защищены.
                    </div>
                </div>
            </body>
            </html>
            """;
    public static String forNotification(String notification) {
        return NOTIFICATION.replace("{{NOTIFICATION}}", notification);
    }

}
