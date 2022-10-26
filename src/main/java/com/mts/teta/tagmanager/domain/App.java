package com.mts.teta.tagmanager.domain;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app")
@Getter
@Setter(PACKAGE)
@NoArgsConstructor(access = PROTECTED)
public class App {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Size(message = "Max app size is {max} but value is ${validatedValue}", max = 200)
  @NotNull(message = "App name is null")
  private String name;

  @OneToMany(fetch = LAZY, mappedBy = "app")
  private List<Container> containers = new ArrayList<>();

  public static App newApp(String name) {
    final var app = new App();
    app.setName(name);
    return app;
  }
}
