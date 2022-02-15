package com.works.glycemic.services;

import com.works.glycemic.config.AuditAwareConfig;
import com.works.glycemic.models.Foods;
import com.works.glycemic.repositories.FoodRepository;
import com.works.glycemic.utils.REnum;
import lombok.AllArgsConstructor;
import org.apache.commons.text.WordUtils;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.works.glycemic.utils.REnum.*;

@Service
@AllArgsConstructor
public class FoodService {

    final FoodRepository fRepo;
    final AuditAwareConfig auditAwareConfig;
    final CacheManager cacheManager;


    //food save
    public Foods foodsSave(Foods foods) {
        Optional<Foods> oFoods = fRepo.findByNameEqualsIgnoreCase(foods.getName());
        if (oFoods.isPresent()) {
            return null;
        } else {
            if (auditAwareConfig.getRoles().contains("ROLE_admin")) {
                foods.setEnabled(true);
            }
            foods.setEnabled(false);
            String foodUrl = charConvert(foods.getName());
            foods.setUrl(foodUrl);
            String newName = WordUtils.capitalize(foods.getName());
            foods.setName(newName);
            return fRepo.save(foods);
        }
    }

    //food list
    public List<Foods> foodList() {
        return fRepo.findByEnabledEqualsOrderByGidDesc(true);
    }

    //admin pending list
    public List<Foods> adminPendingList() {
        return fRepo.findByEnabledEqualsOrderByGidDesc(false);
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

                    String newName = WordUtils.capitalize(food.getName());
                    userFood.setName(newName);

                    userFood.setGlycemicIndex(food.getGlycemicIndex());
                    userFood.setImage(food.getImage());
                    userFood.setSource(food.getSource());
                    userFood.setEnabled(food.isEnabled());

                    if (food.isEnabled()) {
                        cacheManager.getCache("foods_list").clear();
                    }

                    String foodUrl = charConvert(food.getName());
                    userFood.setUrl(foodUrl);

                    hm.put(result, fRepo.saveAndFlush(userFood));
                } else {

                    //user food update
                    Optional<Foods> oFood = fRepo.findByCreatedByEqualsIgnoreCaseAndGidEquals(userName, food.getGid());
                    if (oFood.isPresent()) {
                        userFood.setCid(food.getCid());

                        String newName = WordUtils.capitalize(food.getName());
                        userFood.setName(newName);

                        userFood.setGlycemicIndex(food.getGlycemicIndex());
                        userFood.setImage(food.getImage());
                        userFood.setSource(food.getSource());

                        String foodUrl = charConvert(food.getName());
                        userFood.setUrl(foodUrl);

                        hm.put(result, fRepo.saveAndFlush(userFood));
                    } else {
                        hm.put(status, false);
                        hm.put(message, "Güncellemek istediğiniz ürün size ait değil!");
                    }
                }
            } catch (Exception ex) {
                hm.put(status, false);
                hm.put(message, "Update işlemi sırasında bir hata oluştu!");
            }

        } else {
            hm.put(status, false);
            hm.put(message, "Bu işleme yetkiniz yok!");
        }
        return hm;
    }

    public Optional<Foods> singleFoodUrl(String url) {
        return fRepo.findByUrlEqualsIgnoreCaseAllIgnoreCase(url);
    }

    public static String charConvert(String word) {
        String convertWord = word.toLowerCase().trim().replaceAll("\\s+", "-");
        char[] oldValue = new char[]{'ö', 'ü', 'ç', 'ı', 'ğ', 'ş'};
        char[] newValue = new char[]{'o', 'u', 'c', 'i', 'g', 's'};
        for (int count = 0; count < oldValue.length; count++) {
            convertWord = convertWord.replace(oldValue[count], newValue[count]);
        }
        return convertWord;
    }

}
