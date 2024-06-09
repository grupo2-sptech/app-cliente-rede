package org.example.utilities;

import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

public class Slack {

    private String webUrl;
    private String token;
    private String canal;


    public Slack() {
    }

    public Slack(String webUrl, String token, String canal) {
        this.webUrl = webUrl;
        this.token = token;
        this.canal = canal;
    }

    public void mensagemSlack(String mensagem) {
        try {
            StringBuilder msgbuilder = new StringBuilder();
            msgbuilder.append(mensagem);

            Payload payload = Payload.builder().channel(canal).text(msgbuilder.toString()).build();

            WebhookResponse wbResp = com.github.seratch.jslack.Slack.getInstance().send(webUrl, payload);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem para o slack: " + e.getMessage());
        }
    }

    public String getWebUrl(String string) {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getToken(String string) {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCanal(String string) {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }
}
