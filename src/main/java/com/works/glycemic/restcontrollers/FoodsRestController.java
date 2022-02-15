package com.works.glycemic.restcontrollers;

import com.works.glycemic.models.Foods;
import com.works.glycemic.services.FoodService;
import com.works.glycemic.utils.REnum;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.works.glycemic.utils.REnum.*;

@CrossOrigin(origins = "*",allowedHeaders = "*")
@RestController
@AllArgsConstructor
@RequestMapping("foods")
public class FoodsRestController {

    final FoodService foodService;

    //Food Save
    @PostMapping("save")
    public Map<REnum,Object> save(@RequestBody Foods foods){
        Map<REnum,Object> hm = new LinkedHashMap<>();

        Foods f = foodService.foodsSave(foods);
        if(f==null){
            hm.put(status,false);
            hm.put(message,"Bu ürün daha önce kayıt edilmiş!");
            hm.put(result,f);
        }
        else{
            hm.put(status,true);
            hm.put(message,"Ürün kaydı başarılı!");
            hm.put(result,f);
        }
        return hm;
    }

    //food list
    @Cacheable("foods_list")
    @GetMapping("list")
    public Map<REnum,Object> list(){
        Map<REnum,Object> hm = new LinkedHashMap<>();
        hm.put(status,true);
        hm.put(message,"Ürün Listesi");
        hm.put(result,foodService.foodList());

        return hm;
    }

    @GetMapping("adminPendingList")
    public Map<REnum,Object> adminPendingList(){
        Map<REnum,Object> hm = new LinkedHashMap<>();
        hm.put(status,true);
        hm.put(message,"Ürün Listesi");
        hm.put(result,foodService.adminPendingList());

        return hm;
    }

    //user food list
    @GetMapping("userFoodList")
    public Map<REnum,Object> userFoodList(){
        Map<REnum,Object> hm = new LinkedHashMap<>();
        hm.put(status,true);
        hm.put(message,"Ürün Listesi");
        hm.put(result,foodService.userFoodList());
        return hm;
    }

    //user delete food
    @DeleteMapping("foodDelete/{id}")
    public Map<REnum,Object> userDeleteFood(@PathVariable String id){
       return foodService.userDeleteFood(id);
    }

    //user update food
    @PutMapping("foodUpdate")
    public Map<REnum,Object> userUpdateFood(@RequestBody Foods food){
        return foodService.userUpdateFood(food);
    }

    @GetMapping("detail/{url}")
    public Map<REnum, Object> singleFoodUrl(@PathVariable String url){
        Map<REnum, Object> hm = new LinkedHashMap<>();
        Optional<Foods> oFood = foodService.singleFoodUrl(url);
        if (oFood.isPresent() ) {
            hm.put(REnum.status, true);
            hm.put(REnum.message, "Ürün detay alındı");
            hm.put(REnum.result, oFood.get());
        }else {
            hm.put(REnum.status, false);
            hm.put(REnum.message, "Ürün detay bulunamadı");
            hm.put(REnum.result, null);
        }
        return hm;
    }
}
