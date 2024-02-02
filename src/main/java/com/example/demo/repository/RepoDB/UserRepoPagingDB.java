package com.example.demo.repository.RepoDB;

import com.example.demo.domain.Utilizator;
import com.example.demo.repository.paging.*;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;


public class UserRepoPagingDB extends UserRepoDB  implements PagingRepository<Long, Utilizator>
{
    public UserRepoPagingDB(String url, String user, String password) {
        super(url, user, password);
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        Set<Utilizator> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement =
                     connection.prepareStatement("select * from users limit ? offset ?");
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber() - 1) * pageable.getPageSize());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String pass=resultSet.getString("password");
                Utilizator user=new Utilizator(firstName,lastName,pass);
                user.setId(id);
                users.add(user);

            }
            return new PageImplementation<>(pageable, users.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
