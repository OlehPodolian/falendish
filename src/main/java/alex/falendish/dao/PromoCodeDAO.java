package main.java.alex.falendish.dao;

import main.java.alex.falendish.model.PromoCode;

import java.util.Optional;

public interface PromoCodeDAO {

    PromoCode create(PromoCode promoCode);

    Optional<PromoCode> findOneActive(String promoCode);
}
