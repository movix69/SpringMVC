package com.hibernate.config;

import com.hibernate.entity.*;
import com.hibernate.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final EntityManager entityManager;
    private final IGeneroRepository generoRepository;
    private final IInterpreteRepository interpreteRepository;
    private final IPeliculaRepository peliculaRepository;
    private final IRolRepository rolRepository;
    private final IUsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(
            EntityManager entityManager,
            IGeneroRepository generoRepository,
            IInterpreteRepository interpreteRepository,
            IPeliculaRepository peliculaRepository,
            IRolRepository rolRepository,
            IUsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.entityManager = entityManager;
        this.generoRepository = generoRepository;
        this.interpreteRepository = interpreteRepository;
        this.peliculaRepository = peliculaRepository;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        resetDatabase();
        seedRolesAndUsers();
        seedData();
    }

    private void resetDatabase() {
        entityManager.createNativeQuery(
                "TRUNCATE TABLE t_usuario_roles, t_usuarios, t_roles, t_movies_interpretes, t_movies, t_interpretes, t_generos RESTART IDENTITY CASCADE"
        ).executeUpdate();
    }

    private void seedRolesAndUsers() {
        // Crear roles
        Rol rolViewer = rolRepository.save(new Rol("VIEWER", "Solo puede ver películas"));
        Rol rolEditor = rolRepository.save(new Rol("EDITOR", "Puede ver y editar películas"));
        Rol rolAdmin = rolRepository.save(new Rol("ADMIN", "Tiene acceso total al sistema"));

        // Crear usuarios de prueba
        // Usuario VIEWER
        Usuario usuarioViewer = new Usuario(
                "viewer",
                passwordEncoder.encode("viewer123"),
                "Usuario Viewer",
                "viewer@movies.com"
        );
        usuarioViewer.addRol(rolViewer);
        usuarioRepository.save(usuarioViewer);

        // Usuario EDITOR
        Usuario usuarioEditor = new Usuario(
                "editor",
                passwordEncoder.encode("editor123"),
                "Usuario Editor",
                "editor@movies.com"
        );
        usuarioEditor.addRol(rolEditor);
        usuarioRepository.save(usuarioEditor);

        // Usuario ADMIN
        Usuario usuarioAdmin = new Usuario(
                "admin",
                passwordEncoder.encode("admin123"),
                "Administrador",
                "admin@movies.com"
        );
        usuarioAdmin.addRol(rolAdmin);
        usuarioRepository.save(usuarioAdmin);
    }

    private void seedData() {
        Genero accion = generoRepository.save(new Genero("Accion"));
        Genero cienciaFiccion = generoRepository.save(new Genero("Ciencia Ficcion"));
        Genero drama = generoRepository.save(new Genero("Drama"));
        Genero thriller = generoRepository.save(new Genero("Thriller"));
        Genero fantasia = generoRepository.save(new Genero("Fantasia"));
        Genero crimen = generoRepository.save(new Genero("Crimen"));

        Interprete tomHanks = interpreteRepository.save(new Interprete("Tom Hanks", 1956, "Estados Unidos"));
        Interprete leonardo = interpreteRepository.save(new Interprete("Leonardo DiCaprio", 1974, "Estados Unidos"));
        Interprete scarlett = interpreteRepository.save(new Interprete("Scarlett Johansson", 1984, "Estados Unidos"));
        Interprete christian = interpreteRepository.save(new Interprete("Christian Bale", 1974, "Reino Unido"));
        Interprete matthew = interpreteRepository.save(new Interprete("Matthew McConaughey", 1969, "Estados Unidos"));
        Interprete anne = interpreteRepository.save(new Interprete("Anne Hathaway", 1982, "Estados Unidos"));
        Interprete keanu = interpreteRepository.save(new Interprete("Keanu Reeves", 1964, "Canada"));
        Interprete carrie = interpreteRepository.save(new Interprete("Carrie-Anne Moss", 1967, "Canada"));
        Interprete brad = interpreteRepository.save(new Interprete("Brad Pitt", 1963, "Estados Unidos"));
        Interprete morgan = interpreteRepository.save(new Interprete("Morgan Freeman", 1937, "Estados Unidos"));
        Interprete natalie = interpreteRepository.save(new Interprete("Natalie Portman", 1981, "Israel"));
        Interprete robert = interpreteRepository.save(new Interprete("Robert Downey Jr.", 1965, "Estados Unidos"));
        Interprete chrisEvans = interpreteRepository.save(new Interprete("Chris Evans", 1981, "Estados Unidos"));
        Interprete emma = interpreteRepository.save(new Interprete("Emma Stone", 1988, "Estados Unidos"));
        Interprete ryan = interpreteRepository.save(new Interprete("Ryan Gosling", 1980, "Canada"));
        Interprete viggo = interpreteRepository.save(new Interprete("Viggo Mortensen", 1958, "Estados Unidos"));
        Interprete elijah = interpreteRepository.save(new Interprete("Elijah Wood", 1981, "Estados Unidos"));
        Interprete jodie = interpreteRepository.save(new Interprete("Jodie Foster", 1962, "Estados Unidos"));
        Interprete joaquin = interpreteRepository.save(new Interprete("Joaquin Phoenix", 1974, "Estados Unidos"));
        Interprete hugh = interpreteRepository.save(new Interprete("Hugh Jackman", 1968, "Australia"));

        peliculaRepository.saveAll(List.of(
                pelicula("Forrest Gump", "Robert Zemeckis", 1994,
                        "https://image.tmdb.org/t/p/w500/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg",
                        "https://www.imdb.com/title/tt0109830/", drama, List.of(tomHanks)),

                pelicula("Inception", "Christopher Nolan", 2010,
                        "https://image.tmdb.org/t/p/w500/8IB2e4r4oVhHnANbnm7O3Tj6tF8.jpg",
                        "https://www.imdb.com/title/tt1375666/", cienciaFiccion, List.of(leonardo)),

                pelicula("Interstellar", "Christopher Nolan", 2014,
                        "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
                        "https://www.imdb.com/title/tt0816692/", cienciaFiccion, List.of(matthew, anne)),

                pelicula("The Matrix", "Lana y Lilly Wachowski", 1999,
                        "https://image.tmdb.org/t/p/w500/aOIuZAjPaRIE6CMzbazvcHuHXDc.jpg",
                        "https://www.imdb.com/title/tt0133093/", cienciaFiccion, List.of(keanu, carrie)),

                pelicula("Se7en", "David Fincher", 1995,
                        "https://image.tmdb.org/t/p/w500/6yoghtyTpznpBik8EngEmJskVUO.jpg",
                        "https://www.imdb.com/title/tt0114369/", thriller, List.of(brad, morgan)),

                pelicula("The Dark Knight", "Christopher Nolan", 2008,
                        "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                        "https://www.imdb.com/title/tt0468569/", accion, List.of(christian)),

                pelicula("Black Swan", "Darren Aronofsky", 2010,
                        "https://image.tmdb.org/t/p/w500/viWheBd44bouiLCHgNMvahLThqx.jpg",
                        "https://www.imdb.com/title/tt0947798/", thriller, List.of(natalie)),

                pelicula("Avengers: Endgame", "Anthony y Joe Russo", 2019,
                        "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
                        "https://www.imdb.com/title/tt4154796/", accion, List.of(robert, chrisEvans, scarlett)),

                pelicula("La La Land", "Damien Chazelle", 2016,
                        "https://image.tmdb.org/t/p/w500/uDO8zWDhfWwoFdKS4fzkUJt0Rf0.jpg",
                        "https://www.imdb.com/title/tt3783958/", drama, List.of(emma, ryan)),

                pelicula("The Lord of the Rings: The Fellowship of the Ring", "Peter Jackson", 2001,
                        "https://image.tmdb.org/t/p/w500/6oom5QYQ2yQTMJIbnvbkBL9cHo6.jpg",
                        "https://www.imdb.com/title/tt0120737/", fantasia, List.of(elijah, viggo)),

                pelicula("The Silence of the Lambs", "Jonathan Demme", 1991,
                        "https://image.tmdb.org/t/p/w500/rplLJ2hPcOQmkFhTqUte0MkEaO2.jpg",
                        "https://www.imdb.com/title/tt0102926/", thriller, List.of(jodie)),

                pelicula("Joker", "Todd Phillips", 2019,
                        "https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg",
                        "https://www.imdb.com/title/tt7286456/", drama, List.of(joaquin)),

                pelicula("The Prestige", "Christopher Nolan", 2006,
                        "https://image.tmdb.org/t/p/w500/bdN3gXuIZYaJP7ftKK2sU0nPtEA.jpg",
                        "https://www.imdb.com/title/tt0482571/", drama, List.of(christian, hugh)),

                pelicula("Fight Club", "David Fincher", 1999,
                        "https://image.tmdb.org/t/p/w500/bptfVGEQuv6vDTIMVCHjJ9Dz8PX.jpg",
                        "https://www.imdb.com/title/tt0137523/", drama, List.of(brad)),

                pelicula("Iron Man", "Jon Favreau", 2008,
                        "https://image.tmdb.org/t/p/w500/78lPtwv72eTNqFW9COBYI0dWDJa.jpg",
                        "https://www.imdb.com/title/tt0371746/", accion, List.of(robert)),

                pelicula("Lucy", "Luc Besson", 2014,
                        "https://picsum.photos/id/1/500/750",
                        "https://www.imdb.com/title/tt2872732/", cienciaFiccion, List.of(scarlett)),

                pelicula("The Revenant", "Alejandro G. Inarritu", 2015,
                        "https://picsum.photos/id/2/500/750",
                        "https://www.imdb.com/title/tt1663202/", drama, List.of(leonardo)),

                pelicula("Prisoners", "Denis Villeneuve", 2013,
                        "https://image.tmdb.org/t/p/w500/uhviyknTT5cEQXbn6vWIqfM4vGm.jpg",
                        "https://www.imdb.com/title/tt1392214/", crimen, List.of(hugh)),

                pelicula("Captain America: The Winter Soldier", "Anthony y Joe Russo", 2014,
                        "https://image.tmdb.org/t/p/w500/tVFRpFw3xTedgPGqxW0AOI8Qhh0.jpg",
                        "https://www.imdb.com/title/tt1843866/", accion, List.of(chrisEvans, scarlett)),

                pelicula("The Green Mile", "Frank Darabont", 1999,
                        "https://image.tmdb.org/t/p/w500/velWPhVMQeQKcxggNEU8YmIo52R.jpg",
                        "https://www.imdb.com/title/tt0120689/", drama, List.of(tomHanks))
        ));
    }

    private Pelicula pelicula(
            String titulo,
            String director,
            int anio,
            String imagen,
            String url,
            Genero genero,
            List<Interprete> interpretes
    ) {
        Pelicula pelicula = new Pelicula(titulo, director, anio, imagen, url);
        pelicula.setGenero(genero);
        interpretes.forEach(pelicula::addInterprete);
        return pelicula;
    }
}
