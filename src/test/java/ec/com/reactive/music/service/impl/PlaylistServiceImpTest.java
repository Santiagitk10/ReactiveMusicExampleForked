package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.AlbumDTO;
import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.repository.IPlaylistRepository;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceImpTest {

    @Mock
    IPlaylistRepository playlistRepositoryMock;

    ModelMapper modelMapper;

    PlaylistServiceImp playlistService;


    @BeforeEach
    void init(){
        modelMapper = new ModelMapper();
        playlistService = new PlaylistServiceImp(playlistRepositoryMock,modelMapper);
    }


    @Test
    @DisplayName("findPlaylistById")
    void findPlaylistById() {
        Playlist expectedPlaylist = new Playlist();
        expectedPlaylist.setIdPlaylist("246802");
        expectedPlaylist.setName("Rock");
        expectedPlaylist.setUsername("Santiago Sierra");
        expectedPlaylist.setSongs(new ArrayList<>());

        var playlistDTOExpected = modelMapper.map(expectedPlaylist, PlaylistDTO.class);

        ResponseEntity<PlaylistDTO> playlistDTOResponse = new ResponseEntity<>(playlistDTOExpected, HttpStatus.FOUND);

        Mockito.when(playlistRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(expectedPlaylist));

        var service = playlistService.findPlaylistById("246802");

        StepVerifier.create(service)
                .expectNext(playlistDTOResponse)
                .expectComplete()
                .verify();

        Mockito.verify(playlistRepositoryMock).findById("246802");
    }
}