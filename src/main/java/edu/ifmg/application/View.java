package edu.ifmg.application;

import edu.ifmg.util.Resources;

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.*;

import java.util.*;

class View extends ReceiverAdapter implements RequestHandler {
    private JChannel channel;
    private Vector<Address> controle;

    private MessageDispatcher dispatcher;

    public static void main(String[] args) {
        System.out.println("IM VISION!!");
        try {
            new View().start();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private void start() throws Exception {
        channel = new JChannel(Resources.STACK_FILE);
        channel.setReceiver(this);
        controle = new Vector<Address>();

        dispatcher = new MessageDispatcher(channel, null, null, this);
        
        channel.connect("MarketplaceGroup");
            eventLoop();
        channel.close();
    }

    private void eventLoop() throws Exception{
        RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_ALL);
        options.setAnycasting(false);

        RspList<String> list = dispatcher.castMessage(null, new Message(null, "VISAO"), options);
        
        for(Rsp<String> x : list) {
            if(x.getValue().equals("CONTROLE"))
                controle.add(x.getSender());
        }

        while(true) {
            Util.sleep(1000);
            messageToControl("MENSAGEM DA VISAO");
        }
    }

    private void messageToControl(String msg) throws Exception {
        RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_FIRST);
        options.setAnycasting(true);

        RspList<String> rlist = dispatcher.castMessage(controle,
            new Message(null, "REQUEST"), options);

        
        for(Rsp<String> x : rlist) {
            if(!x.wasReceived())
                continue;
            
            Message message = new Message(x.getSender(), msg);
            String response = dispatcher.sendMessage(message, options);
            // faz o tratamento da resposta do controle por aqui
            break;
        }
    } 

    @Override
    public Object handle(Message msg) throws Exception {
        if(msg.getObject().equals("CONTROLE")) {
            controle.add(msg.getSrc());
            return "VISAO";
        }
        if(msg.getObject().equals("VISAO")) {
            return "VISAO";
        }
        if(msg.getObject().equals("CONTROLE")) {
            return "VISAO";
        }
        return null;
    }
}