package edu.ifmg.application;

import edu.ifmg.util.Resources;

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.*;

import java.util.*;
import java.util.concurrent.locks.Lock;


class Control extends ReceiverAdapter implements RequestHandler {
    private JChannel channel;
    private LockService lock_service;
    private Vector<Address> modelo;

    private MessageDispatcher dispatcher;

    public static void main(String[] args) throws Exception {
        System.out.println("IM CONTROL!!");
        new Control().start();
    }

    private void start() throws Exception {
        channel = new JChannel(Resources.STACK_FILE);
        channel.setReceiver(this);
        modelo = new Vector<Address>();
        dispatcher = new MessageDispatcher(channel, null, null, this);
        // descomentar a linha abaixo para poder realizar mutex
        // na pilha de protocolos algum prolocolo de trava
        // deve ser especificado, caso contrario ocorrera um erro
        // lock_service = new LockService(channel);

        channel.connect("MarketplaceGroup");
            eventLoop();
        channel.close();
    }

    private void eventLoop() throws Exception {
        RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_ALL);
        options.setAnycasting(false);

        RspList<String> list = dispatcher.castMessage(null, new Message(null, "CONTROLE"), options);
        
        for(Rsp<String> x : list) {
            if(x.getValue().equals("MODELO"))
                modelo.add(x.getSender());
        }

        while(true) {
        }
    }

    private void messageToModel(String msg) throws Exception {
        RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_ALL);
        options.setAnycasting(true);
        RspList<String> list = dispatcher.castMessage(modelo, new Message(null, msg), options);

        // travar o modelo para fazer as operações de
        // escrita

        // para obter uma trava:
        /*Lock lock = lock_service.getLock("lock_name");
        lock.lock();
        try {
            System.out.println("DOING DUMB STUFF");
        } catch (Exception e) {
            //TODO: handle exception
        } finally {
            lock.unlock();
        }*/
    }

    @Override
    public Object handle(Message msg) throws Exception {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
        if(msg.getObject().equals("MODELO")) {
            modelo.add(msg.getSrc());
            return "CONTROLE";
        }
        if(msg.getObject().equals("VISAO")) {
            return "CONTROLE";
        }
        if(msg.getObject().equals("CONTROLE")) {
            return "CONTROLE";
        }

        if(msg.getObject().equals("REQUEST")) {
            return "AVAILABLE";
        }

        String toModel = (String)msg.getObject();

        System.out.println(toModel);
        messageToModel(channel.getAddress() + toModel);

        return "RESPOSTA DO CONTROLE";
    }
}