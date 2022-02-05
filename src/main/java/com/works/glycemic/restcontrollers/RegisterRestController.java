package com.works.glycemic.restcontrollers;

import com.works.glycemic.models.User;
import com.works.glycemic.repositories.RoleRepository;
import com.works.glycemic.repositories.UserRepository;
import com.works.glycemic.services.UserService;
import com.works.glycemic.utils.REnum;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.works.glycemic.utils.REnum.*;

@RestController
@RequestMapping("register")
@AllArgsConstructor
public class RegisterRestController {

    final UserService userService;

    //User Register
    @PostMapping("userRegister")
    public Map<REnum,Object> userRegister(@RequestBody User user){
        Map<REnum,Object> hm = new LinkedHashMap<>();
        User u = userService.userRegisterService(user);
        if(u==null){
            hm.put(status,false);
            hm.put(message,"Bu mail adresi ile daha önce kayıt olunmuş!");
            hm.put(result,u);
        }
        else{
            hm.put(status,true);
            hm.put(message,"Kayıt işlemi başarılı");
            hm.put(result,u);
        }

        return hm;
    }

    //Admin Register
    @PostMapping("adminRegister")
    public Map<REnum,Object> adminRegister(@RequestBody User user){
        Map<REnum,Object> hm = new LinkedHashMap<>();
        User u = userService.adminRegisterService(user);
        if(u==null){
            hm.put(status,false);
            hm.put(message,"Bu mail adresi ile daha önce kayıt olunmuş!");
            hm.put(result,u);
        }
        else{
            hm.put(status,true);
            hm.put(message,"Kayıt işlemi başarılı");
            hm.put(result,u);
        }

        return hm;
    }


}
