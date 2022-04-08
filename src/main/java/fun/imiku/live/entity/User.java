package fun.imiku.live.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String nickname;
    private String password;
    @ColumnDefault("3")
    private int gender;
    @ColumnDefault("default.png")
    private String avatar;
    private int innerCode;
    private String intro;
}
