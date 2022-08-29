package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.AlbumDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Album;
import ec.com.reactive.music.domain.entities.Song;
import ec.com.reactive.music.repository.IAlbumRepository;
import ec.com.reactive.music.repository.ISongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {

    @Mock
    ISongRepository songRepositoryMock;

    ModelMapper modelMapper; //Helper - Apoyo/Soporte

    SongServiceImpl songService;

    @BeforeEach
    void init(){
        modelMapper = new ModelMapper();
        songService = new SongServiceImpl(songRepositoryMock,modelMapper);
    }

    @Test
    @DisplayName("findAllSongs()")
    void findAllSongs() {


        //Arrange song list
        ArrayList<Song> listSongs = new ArrayList<>();
        listSongs.add(new Song());
        listSongs.add(new Song());

        //Convert song list into ArrayList of SongDTO
        ArrayList<SongDTO> listSongsDTO = listSongs.stream().
                map(song -> modelMapper.map(song,SongDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));

        //Build the flux of songs from the list of Song
        var fluxResult = Flux.fromIterable(listSongs);
        //Build the flux of SongDTO from the list of SongDTO
        var fluxResultDTO = Flux.fromIterable(listSongsDTO);

        //Expected Result from calling the service Method
        ResponseEntity<Flux<SongDTO>> respEntResult = new ResponseEntity<>(fluxResultDTO, HttpStatus.FOUND);

        //Mocking of the result expected from the repository
        Mockito.when(songRepositoryMock.findAll()).thenReturn(fluxResult);

        //Act
        var service = songService.findAllSongs();

        //Assert - StepVerifier
        StepVerifier.create(service)
                .expectNextMatches(fluxResponseEntity -> fluxResponseEntity.getStatusCode().is3xxRedirection())
                .expectComplete().verify();

    }


    @Test
    @DisplayName("findAllSongsError()")
    void findAllSongsError() {

        ResponseEntity<Flux<SongDTO>> songDTOResponse = new ResponseEntity<>(Flux.empty(),HttpStatus.NO_CONTENT);

        Mockito.when(songRepositoryMock.findAll()).thenReturn(Flux.empty());

        var service = songService.findAllSongs();

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).findAll();

    }



    @Test
    @DisplayName("findSongById()")
    void findSongById() {
        Song songExpected = new Song();
        songExpected.setIdSong("23456");
        songExpected.setName("Smells Like Teen Spirit");
        songExpected.setIdAlbum("98765");
        songExpected.setLyricsBy("Pina Records");
        songExpected.setProducedBy("Manolo Santana");
        songExpected.setArrangedBy("Daddy Yankee");
        songExpected.setDuration(LocalTime.of(00,02,24));

        var songDTOExpected = modelMapper.map(songExpected, SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOExpected, HttpStatus.FOUND);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(songExpected));

        var service = songService.findSongById("23456");

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        Mockito.verify(songRepositoryMock).findById("23456");

    }


    @Test
    @DisplayName("findSongByIdError()")
    void findSongByIdError() { //Not found

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(new SongDTO(),HttpStatus.NOT_FOUND);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.empty());

        var service = songService.findSongById("12345");

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).findById("12345");
    }





    @Test
    @DisplayName("saveSong()")
    void saveSong() {
        Song songExpected = new Song();
        songExpected.setIdSong("23456");
        songExpected.setName("Smells Like Teen Spirit");
        songExpected.setIdAlbum("98765");
        songExpected.setLyricsBy("Pina Records");
        songExpected.setProducedBy("Manolo Santana");
        songExpected.setArrangedBy("Daddy Yankee");
        songExpected.setDuration(LocalTime.of(00,02,24));

        var songDTOexpected = modelMapper.map(songExpected,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOexpected,HttpStatus.CREATED);

        Mockito.when(songRepositoryMock.save(Mockito.any(Song.class))).thenReturn(Mono.just(songExpected));

        var service = songService.saveSong(songDTOexpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        Mockito.verify(songRepositoryMock).save(songExpected);
    }


    @Test
    @DisplayName("saveSongError()")
    void saveSongError() {

        Song songExpected = new Song();
        songExpected.setIdSong("23456");
        songExpected.setName("Smells Like Teen Spirit");
        songExpected.setIdAlbum("98765");
        songExpected.setLyricsBy("Pina Records");
        songExpected.setProducedBy("Manolo Santana");
        songExpected.setArrangedBy("Daddy Yankee");
        songExpected.setDuration(LocalTime.of(00,02,24));

        var songDTOexpected = modelMapper.map(songExpected,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

        Mockito.when(songRepositoryMock.save(Mockito.any(Song.class))).thenReturn(Mono.empty());

        var service = songService.saveSong(songDTOexpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).save(songExpected);

    }


    @Test
    @DisplayName("updateSong()")
    void updateSong() {

        //Arrange
        Song songExpected = new Song();
        songExpected.setIdSong("23456");
        songExpected.setName("Smells Like Teen Spirit");
        songExpected.setIdAlbum("98765");
        songExpected.setLyricsBy("Pina Records");
        songExpected.setProducedBy("Manolo Santana");
        songExpected.setArrangedBy("Daddy Yankee");
        songExpected.setDuration(LocalTime.of(00,02,24));

        var songEdited = songExpected.toBuilder().name("Smells Like Editted").build();

        SongDTO songDTOExpected = modelMapper.map(songEdited, SongDTO.class);

        ResponseEntity<SongDTO> expectedResponse = new ResponseEntity<>(songDTOExpected,HttpStatus.ACCEPTED);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(songExpected));
        Mockito.when(songRepositoryMock.save(Mockito.any(Song.class))).thenReturn(Mono.just(songEdited));

        //Act
        var service = songService.updateSong(songExpected.getIdSong(),songDTOExpected);

        //Assert
        StepVerifier.create(service)
                .expectNext(expectedResponse)
                .expectComplete()
                .verify();


        Mockito.verify(songRepositoryMock).save(songEdited);
        Mockito.verify(songRepositoryMock).findById(songEdited.getIdSong());
    }


    @Test
    @DisplayName("updateSongError()")
    void updateSongError() {

        Song songExpected = new Song();
        songExpected.setIdSong("23456");
        songExpected.setName("Smells Like Teen Spirit");
        songExpected.setIdAlbum("98765");
        songExpected.setLyricsBy("Pina Records");
        songExpected.setProducedBy("Manolo Santana");
        songExpected.setArrangedBy("Daddy Yankee");
        songExpected.setDuration(LocalTime.of(00,02,24));

        var songDTOexpected = modelMapper.map(songExpected,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.empty());

        var service = songService.updateSong(songDTOexpected.getIdSong(),songDTOexpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).findById(songExpected.getIdSong());

    }


    @Test
    @DisplayName("deleteSong()")
    void deleteSong() {
        //Arrange
        Song songExpected = new Song();
        songExpected.setIdSong("23456");
        songExpected.setName("Smells Like Teen Spirit");
        songExpected.setIdAlbum("98765");
        songExpected.setLyricsBy("Pina Records");
        songExpected.setProducedBy("Manolo Santana");
        songExpected.setArrangedBy("Daddy Yankee");
        songExpected.setDuration(LocalTime.of(00,02,24));

        ResponseEntity<String> responseDelete = new ResponseEntity<>(songExpected.getIdSong(),HttpStatus.ACCEPTED);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(songExpected));
        Mockito.when(songRepositoryMock.deleteById(Mockito.any(String.class))).thenReturn(Mono.empty());


        //Act
        var service = songService.deleteSong("23456");

        StepVerifier.create(service)
                .expectNext(responseDelete)
                .expectComplete()
                .verify();

        Mockito.verify(songRepositoryMock).findById("23456");
        Mockito.verify(songRepositoryMock).deleteById("23456");
    }


    @Test
    @DisplayName("deleteSongError()")
    void deleteSongError() {

        Song songExpected = new Song();
        songExpected.setIdSong("23456");
        songExpected.setName("Smells Like Teen Spirit");
        songExpected.setIdAlbum("98765");
        songExpected.setLyricsBy("Pina Records");
        songExpected.setProducedBy("Manolo Santana");
        songExpected.setArrangedBy("Daddy Yankee");
        songExpected.setDuration(LocalTime.of(00,02,24));


        ResponseEntity<String> songDTOResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.empty());

        var service = songService.deleteSong(songExpected.getIdSong());

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).findById(songExpected.getIdSong());

    }



}