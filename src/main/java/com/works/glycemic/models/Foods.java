package com.works.glycemic.models;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Foods extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gid;

    private Integer cid;
    @Column(unique = true)
    private String name;
    private Integer glycemicIndex;
    @Column(length = 30000)
    private String image;
    private String source;
    private String url;
    private boolean enabled;


}
