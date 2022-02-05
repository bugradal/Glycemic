package com.works.glycemic.restcontrollers;

import com.works.glycemic.models.Foods;
import com.works.glycemic.services.FoodService;
import com.works.glycemic.utils.REnum;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.works.glycemic.utils.REnum.*;

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
            hm.put(status,f);
        }
        else{
            hm.put(status,true);
            hm.put(message,"Ürün kaydı başarılı!");
            hm.put(status,f);
        }
        return hm;
    }

    //food list
    @GetMapping("list")
    public Map<REnum,Object> list(){
        Map<REnum,Object> hm = new LinkedHashMap<>();
        hm.put(status,true);
        hm.put(message,"Ürün Listesi");
        hm.put(result,foodService.foodList());

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
    @DeleteMapping("userDeleteFood/{id}")
    public Map<REnum,Object> userDeleteFood(@PathVariable String id){
       return foodService.userDeleteFood(id);
    }

    //user delete food
    @PutMapping("userUpdateFood")
    public Map<REnum,Object> userUpdateFood(@RequestBody Foods food){
        return foodService.userUpdateFood(food);
    }




}
