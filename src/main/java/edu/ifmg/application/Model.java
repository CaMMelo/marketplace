package edu.ifmg.application;

import edu.ifmg.util.Resources;

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.*;

import java.util.*;

class Model extends ReceiverAdapter implements RequestHandler {
    private JChannel channel;

    private MessageDispatcher dispatcher;

    public static void main(String[] args) {
        System.out.println("IM MODEL!!");
        try {
            new Model().start();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private void start() throws Exception {
        channel = new JChannel(Resources.STACK_FILE);
        channel.setReceiver(this);

        dispatcher = new MessageDispatcher(channel, null, null, this);

        channel.connect("MarketplaceGroup");
            eventLoop();
        channel.close();
    }

    private void eventLoop() throws Exception {
        RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_ALL);
        options.setAnycasting(false);
        dispatcher.castMessage(null, new Message(null, "MODELO"), options);
        while(true) {
        }
    }

    @Override
    public Object handle(Message msg) throws Exception {
        if(msg.getObject().equals("MODELO")) {
            return "MODELO";
        }
        if(msg.getObject().equals("VISAO")) {
            return "MODELO";
        }
        if(msg.getObject().equals("CONTROLE")) {
            return "MODELO";
        }
        System.out.println(msg.getObject());
        // faz leitura/escrita no banco de dados
        // retorna um valor booleano para sucesso / falha
        return "RESPOSTA DO MODELO.";
    }
}