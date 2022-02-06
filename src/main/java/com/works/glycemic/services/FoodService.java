package com.works.glycemic.services;

import com.works.glycemic.config.AuditAwareConfig;
import com.works.glycemic.models.Foods;
import com.works.glycemic.repositories.FoodRepository;
import com.works.glycemic.utils.REnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.works.glycemic.utils.REnum.*;

@Service
@AllArgsConstructor
public class FoodService {

    final FoodRepository fRepo;
    final AuditAwareConfig auditAwareConfig;


    //food save
    public Foods foodsSave(Foods foods) {
        Optional<Foods> oFoods = fRepo.findByNameEqualsIgnoreCase(foods.getName());
        if (oFoods.isPresent()) {
            return null;
        } else {
            foods.setEnabled(false);
            return fRepo.save(foods);
        }
    }

    //food list
    public List<Foods> foodList() {
        return fRepo.findAll();
    }

    //user food list
    public List<Foods> userFoodList() {
        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
        if (oUserName.isPresent()) {
            return fRepo.findByCreatedByEqualsIgnoreCase(oUserName.get());
        } else {
            return new ArrayList<Foods>();
        }
    }

    //user delete food
    public Map<REnum, Object> userDeleteFood(String id) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(status, true);
        hm.put(message, "Ürün başarıyla silindi");
        hm.put(result, "id: " + id);
        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
        if (oUserName.isPresent()) {
            String userName = oUserName.get();
            try {
                Long gid = Long.parseLong(id);
                //admin food delete
                if (auditAwareConfig.getRoles().contains("ROLE_admin")) {
                    fRepo.deleteById(gid);
                } else {
                    //user food delete
                    Optional<Foods> oFood = fRepo.findByCreatedByEqualsIgnoreCaseAndGidEquals(userName, gid);
                    if (oFood.isPresent()) {
                        fRepo.deleteById(gid);
                    } else {
                        hm.put(status, false);
                        hm.put(message, "Silmek istediğiniz ürün size ait değil!");
                    }
                }
            } catch (Exception ex) {
                hm.put(status, false);
                hm.put(message, "Silme işlemi sırasında bir hata oluştu!");
            }

        } else {
            hm.put(status, false);
            hm.put(message, "Bu işlem için yetkiniz yok!");
        }
        return hm;
    }


    //user update food
    public Map<REnum, Object> userUpdateFood(Foods food) {
        Map<REnum, Object> hm = new LinkedHashMap<>();

        hm.put(status, true);
        hm.put(message, "Ürün başarıyla güncellendi");
        hm.put(result, "id: " + food.getGid());


        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();

        if (oUserName.isPresent()) {
            String userName = oUserName.get();
            try {
                Foods userFood = fRepo.findById(food.getGid()).get();

                //admin food update
                if (auditAwareConfig.getRoles().contains("ROLE_admin")) {
                    userFood.setCid(food.getCid());
                    userFood.setName(food.getName());
                    userFood.setGlycemicIndex(food.getGlycemicIndex());
                    userFood.setImage(food.getImage());
                    userFood.setSource(food.getSource());
                    userFood.setEnabled(food.isEnabled());

                    hm.put(result, fRepo.save(userFood));
                }
                else {

                    //user food update
                    Optional<Foods> oFood = fRepo.findByCreatedByEqualsIgnoreCaseAndGidEquals(userName,food.getGid());
                    if (oFood.isPresent()) {
                        userFood.setCid(food.getCid());
                        userFood.setName(food.getName());
                        userFood.setGlycemicIndex(food.getGlycemicIndex());
                        userFood.setImage(food.getImage());
                        userFood.setSource(food.getSource());

                        hm.put(result, fRepo.save(userFood));
                    }
                    else {
                        hm.put(status, false);
                        hm.put(message, "Güncellemek istediğiniz ürün size ait değil!");
                    }
                }
            }
            catch (Exception ex) {
                hm.put(status, false);
                hm.put(message, "Update işlemi sırasında bir hata oluştu!");
            }

        } else {
            hm.put(status, false);
            hm.put(message, "Bu işleme yetkiniz yok!");
        }
        return hm;
    }

}
