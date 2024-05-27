package org.example.controller;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import org.example.entites.*;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.*;


public class MainVertical extends AbstractVerticle {

//    List<Item> items = new ArrayList<>();
    Map<Integer, Item> items = new HashMap<>();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

//        Vertx vertx= Vertx.vertx();
        Router router= Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/api/items").handler(routingContext -> {
            Item item= routingContext.getBodyAsJson().mapTo(Item.class);
            System.out.println(item);
            Integer id = item.getId();
            items.put(id, item);

            routingContext.response().setStatusCode(201)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject.mapFrom(item).encode());
        });

        router.get("/api/items/:id").handler(routingContext -> {
            String sId= routingContext.pathParam("id");
            Integer id= Integer.parseInt(sId);
            Item item= items.get(id);
            if(item !=null){
                routingContext.response().setStatusCode(200)
                        .putHeader("content-type", "application/json")
                        .end(JsonObject.mapFrom(item).encode());
            }else {
                routingContext.response().setStatusCode(404)
                        .end("incorrect id :"+id);
            }

        });

        router.put("/api/items/:id").handler(routingContext -> {
            String sId= routingContext.pathParam("id");
            Integer id= Integer.parseInt(sId);
            JsonObject uItem= routingContext.getBodyAsJson();
            Item oldI= items.get(id);
            if(oldI!=null){

                oldI.setName(uItem.getString("name"));
                oldI.setDescription(uItem.getString("description"));
                items.put(id, oldI);
                routingContext.response().setStatusCode(HttpResponseStatus.ACCEPTED.code())
                        .putHeader("content-type", "application/json")
                        .end(JsonObject.mapFrom(uItem).encode());
            }else {
                routingContext.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
                        .end("incorrect id : "+id);
            }
        });

        router.delete("/api/items/:id").handler(routingContext -> {
            String sId= routingContext.pathParam("id");
            Integer id= Integer.parseInt(sId);
            if(items.remove(id) != null){
                routingContext.response()
                        .setStatusCode(HttpResponseStatus.OK.code())
                        .end("deleted !!");
            }else {
                routingContext.response()
                        .setStatusCode(404).end("not found");
            }
        });

        vertx.createHttpServer().requestHandler(router).listen(8084 , http->{
            if(http.succeeded())   {
                startPromise.complete();
                System.out.println("HTTP server started on port 8084");
            }   else {
                startPromise.fail(http.cause());
            }
        });



    }



}
