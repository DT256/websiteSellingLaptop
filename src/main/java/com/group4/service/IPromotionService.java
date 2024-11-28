package com.group4.service;

import com.group4.entity.PromotionEntity;
import com.group4.model.PromotionModel;

import java.util.List;
import java.util.Optional;

public interface IPromotionService {
    public List<PromotionModel> fetchPromotionList();
    public boolean savePromotion(PromotionModel promotionModel);
    public boolean isPromotionCodeExists(String promotionCode);
    public boolean updatePromotion(PromotionModel promotionModel);
    public boolean saveOrUpdatePromotion(PromotionModel promotionModel);
    public boolean deletePromotion(Long id);
    public PromotionModel findPromotionById(Long id);
    public Optional<PromotionEntity> findByPromotionCode(String promotionCode);
    public void save(PromotionEntity promotion);

}
