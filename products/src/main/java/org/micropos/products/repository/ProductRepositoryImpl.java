package org.micropos.products.repository;

import java.util.UUID;

import org.micropos.products.db.ProductDb;
import org.micropos.products.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @Autowired
    private ProductDb db;

    @Override
    public Flux<String> all() {
        return db.findAll().map(Product::getId);
    }

    @Override
    public Mono<Product> get(String id) {
        return db.findById(id);
    }

    @Override
    public Mono<Product> create(Product item) {
        return db.save(item.withId(UUID.randomUUID().toString()));
    }

    @Override
    public Mono<Product> update(Product item) {
        return Mono.just(item).filterWhen(x -> db.existsById(x.getId())).flatMap(x -> db.save(x));
    }

    @Override
    public Mono<Product> remove(String id) {
        return db.existsById(id).flatMap(has -> {
            if (has) {
                return get(id).flatMap(item -> db.deleteById(id).thenReturn(item));
            }
            return Mono.empty();
        });
    }
}
