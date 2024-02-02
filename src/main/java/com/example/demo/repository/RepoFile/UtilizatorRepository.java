package com.example.demo.repository.RepoFile;

import com.example.demo.domain.Utilizator;
import com.example.demo.domain.validators.Validator;

import java.util.List;
public class UtilizatorRepository extends AbstractFileRepository<Long, Utilizator> {

    public UtilizatorRepository(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
    }

    @Override
    protected Utilizator extractEntity(List<String> attributes) {
        Utilizator user = new Utilizator(attributes.get(1),attributes.get(2),attributes.get(3));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName()+";"+entity.getPassword();
    }
}
