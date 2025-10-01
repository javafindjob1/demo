package com.abd;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DestructableParse {
  public Map<String, DestructableDetail> parse(List<Destructable> destructables) {

    return destructables.stream().collect(Collectors.toMap(Destructable::getId, e -> {
      DestructableDetail destructableDetail = new DestructableDetail();
      destructableDetail.setId(e.getId());
      destructableDetail.setName(e.getName());
      destructableDetail.setHp(e.getHP());
      return destructableDetail;
    }));

  }

}
