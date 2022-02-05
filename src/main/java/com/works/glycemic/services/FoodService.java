package com.works.glycemic.services;

import com.works.glycemic.config.AuditAwareConfig;
import com.works.glycemic.models.Foods;
import com.works.glycemic.repositories.FoodRepository;
import com.works.glycemic.utils.REnum;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.works.glycemic.utils.REnum.*;

@Service
@AllArgsConstructor
public class FoodService {

    final FoodRepository fRepo;
    final AuditAwareConfig auditAwareConfig;


    //food save
    public Foods foodsSave(Foods foods){
        Optional<Foods> oFoods = fRepo.findByNameEqualsIgnoreCase(foods.getName());
        if(oFoods.isPresent()){
            return null;
        }
        else{
            foods.setEnabled(false);
            return fRepo.save(foods);
        }
    }

    //food list
    public List<Foods> foodList(){
        return fRepo.findAll();
    }

    //user food list
    public List<Foods> userFoodList(){
        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
        if (oUserName.isPresent()) {
            return fRepo.findByCreatedByEqualsIgnoreCase(oUserName.get());
        }
        else {
            return new ArrayList<Foods>();
        }
    }

    //user delete food
    public Map<REnum,Object> userDeleteFood(String id){
        Map<REnum,Object> hm = new LinkedHashMap<>();

        Long gid = Long.parseLong(id);

        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
        if (oUserName.isPresent()) {
            Optional<Foods> oFood =fRepo.findById(gid);
            if(oFood.isPresent()){
                Foods f = oFood.get();
                if(f.getCreatedBy().equals(oUserName.get())){
                    fRepo.deleteById(gid);

                    hm.put(status,true);
                    hm.put(message,"Ürün başarıyla silindi");
                    hm.put(result,f);
                }
                else {
                    hm.put(status,false);
                    hm.put(message,"Silmek istediğiniz ürün size ait değil!");
                    hm.put(result,"id: "+id);
                }
            }
            else{
                hm.put(status,false);
                hm.put(message,id+" id'li bir ürün bulunamadı!");
                hm.put(result,"id: "+id);
            }
        }
        else {
            hm.put(status,false);
            hm.put(message,"Ürün silinemedi");
            hm.put(result,null);
        }
        return hm;
    }

    //user update food
    public Map<REnum,Object> userUpdateFood(Foods food){
        Map<REnum,Object> hm = new LinkedHashMap<>();

        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
        Optional<Foods> oFood = fRepo.findById(food.getGid());
        if (oUserName.isPresent()) {
            if(oFood.isPresent()){
                if(food.getCreatedBy().equals(oUserName.get())){
                    hm.put(status,true);
                    hm.put(message,"Ürün başarıyla güncellendi");
                    hm.put(result, fRepo.save(food));
                }
                else {
                    hm.put(status,false);
                    hm.put(message,"Güncellemek istediğiniz ürün size ait değil!");
                    hm.put(result,"id: "+food.getGid() );
                }
            }
            else{
                hm.put(status,false);
                hm.put(message,food.getGid()+" id'li ürün bulunamadı!");
                hm.put(result, "id: "+food.getGid());
            }
        }
        else {
            hm.put(status,false);
            hm.put(message,"Ürün güncellenemedi!");
            hm.put(result,null);
        }
        return hm;
    }

}
