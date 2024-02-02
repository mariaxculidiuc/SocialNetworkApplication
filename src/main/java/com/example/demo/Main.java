package com.example.demo;

import com.example.demo.UI.UI;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.RepoFriendRequest;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.repository.RepoDB.UserRepoPagingDB;
import com.example.demo.repository.paging.Pageable;
import com.example.demo.repository.paging.PageableImplementation;
import com.example.demo.repository.paging.PagingRepository;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;

import java.sql.SQLOutput;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        //UserRepoDB u_repo = new UserRepoDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","dinozaur123");
        // System.out.println(u_repo.findOne(1l));
        // System.out.println(u_repo.findAll());
        //UI ui = UI.getInstance();
        //ui.run();

        String url="jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "selena23";

        PagingRepository<Long, Utilizator> userDBPagingRepo =
                new UserRepoPagingDB(url, username, password);
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduceti dimensiunea paginii: ");
        int pageSize = scanner.nextInt();

        System.out.print("Introduceti numarul paginii: ");
        int pageNumber = scanner.nextInt();

        Pageable pageable = new PageableImplementation(pageNumber, pageSize);
        var content = userDBPagingRepo.findAll(pageable)
                .getContent().toList();
        if(!content.isEmpty()) {
            content.forEach(System.out::println);
        }
        else {
            System.out.println("Pagina ceruta nu exista.");
        }
    }

}