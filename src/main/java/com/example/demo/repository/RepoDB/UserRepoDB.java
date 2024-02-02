package com.example.demo.repository.RepoDB;

import com.example.demo.domain.Utilizator;
import com.example.demo.domain.validators.UtilizatorValidator;
import com.example.demo.domain.validators.Validator;
import com.example.demo.repository.Repository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.*;

public class UserRepoDB implements Repository<Long, Utilizator> {

    protected String url;
    protected String user;
    protected String password;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Validator<Utilizator> validator;

    public UserRepoDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        validator = new UtilizatorValidator();
    }

    @Override
    public Optional<Utilizator> findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String password = resultSet.getString("password");
                Utilizator u = new Utilizator(firstName,lastName,password);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String password = resultSet.getString("password");
                Utilizator user=new Utilizator(firstName,lastName,password);
                user.setId(id);
                users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! Id-ul nu poate sa fie null!");

        validator.validate(entity);

        if(findOne(entity.getId()).isPresent())
            return Optional.of(entity);

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("insert into users(id, first_name, last_name,password) " +
                    "values(?,?,?,?)");
        ){
            statement.setInt(1, entity.getId().intValue());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            String encodedPassword = passwordEncoder.encode(entity.getPassword());
            statement.setString(4, encodedPassword);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("Eroare! Id-ul nu poate sa fie null!");
        Optional<Utilizator> entity = findOne(aLong);
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("DELETE FROM users WHERE id = ?");)
        {
            statement.setInt(1, aLong.intValue());
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():Optional.ofNullable(entity.get());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        try(Connection connection = DriverManager.getConnection(url,user,password);
            PreparedStatement statement  = connection.prepareStatement("UPDATE users SET first_name = ?, last_name = ?, password = ? WHERE id = ?");)
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.setString(3, entity.getPassword());
            statement.setInt(4, entity.getId().intValue());
            int affectedRows = statement.executeUpdate();
            return affectedRows==0? Optional.empty():Optional.ofNullable(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Iterable<Utilizator> getAllN(Long n)
    {

        Iterable<Utilizator> allUsers = (Iterable<Utilizator>) findAll();
        List<Utilizator> users =new LinkedList<>();
        for (Utilizator allUser : allUsers) {
            if(getFriendsIds(allUser.getId()).size()>=n)
                users.add(allUser);
        }

        return users;
    }

    public List<Long> getFriendsIds(Long id){
        //toti prietenii unui om dupa id dat
        List<Long> idList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.id, friendships.user1_id, friendships.user2_id " +
                            "from users " +
                            "INNER JOIN friendships on (users.id = friendships.user1_id or users.id = friendships.user2_id) " +
                            "where users.id = ?");
        ){
            statement.setInt(1,id.intValue());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long id1 = resultSet.getLong("user1_id");
                Long id2 = resultSet.getLong("user2_id");

                if(id1 != id) idList.add(id1);
                else idList.add(id2);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return idList;
    }

    public List<Long> getFriendsIdsForFriendRequest(Long id){

        List<Long> idList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.id, friendshipRequest.id1, friendshipRequest.id2 " +
                            "from users " +
                            "INNER JOIN friendshipRequest on users.id = friendshipRequest.id2 " +
                            "where (users.id = ? and friendshipRequest.status = 'PENDING')");
        ){
            statement.setInt(1,id.intValue());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long idBun = resultSet.getLong("id1");
                idList.add(idBun);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return idList;
    }


    public Optional<Utilizator> findFirstByName(String fn, String ln,String pass) {

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where first_name = ? and last_name=? LIMIT 1");

        ){
            statement.setString(1, fn);
            statement.setString(2,ln);
            statement.setString(3, pass);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Integer integer = resultSet.getInt("id");
                Long id = integer.longValue();
                Utilizator user = new Utilizator(fn,ln,pass);
                user.setId(id);
                return Optional.ofNullable(user);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}
