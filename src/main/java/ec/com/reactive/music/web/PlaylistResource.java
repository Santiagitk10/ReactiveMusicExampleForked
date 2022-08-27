package ec.com.reactive.music.web;

import ec.com.reactive.music.domain.dto.AlbumDTO;
import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.service.ISongService;
import ec.com.reactive.music.service.impl.AlbumServiceImpl;
import ec.com.reactive.music.service.impl.PlaylistServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
public class PlaylistResource {

    @Autowired
    private PlaylistServiceImp playlistService;

    @Autowired
    private ISongService iSongService;

    @GetMapping("/findAllPlaylists")
    private Mono<ResponseEntity<Flux<PlaylistDTO>>> getPlaylists(){
        return playlistService.findAllPlaylists();
    }

    //GET
    @GetMapping("/findPlaylist/{id}")
    private Mono<ResponseEntity<PlaylistDTO>> getPlaylistById(@PathVariable String id){
        return playlistService.findPlaylistById(id);
    }

    //POST
    @PostMapping("/savePlaylist")
    private Mono<ResponseEntity<PlaylistDTO>> postPlaylist(@RequestBody PlaylistDTO playlistDTO){
        return playlistService.savePlaylist(playlistDTO);
    }

    //PUT
    @PutMapping("/updatePlaylist/{id}")
    private Mono<ResponseEntity<PlaylistDTO>> putPlaylist(@PathVariable String id , @RequestBody PlaylistDTO playlistDTO){
        return playlistService.updatePlaylist(id,playlistDTO);
    }


    @PutMapping("/addSongPlaylist/{playlistId}")
    private Mono<ResponseEntity<PlaylistDTO>> addSongPlaylist(@PathVariable String playlistId, @RequestParam(name="songID") String songID){
        System.out.println(playlistId);
        System.out.println(songID);
        return iSongService.findSongById(songID)
                .flatMap(songDTOResponseEntity -> playlistService.addSongPlaylist(playlistId,songDTOResponseEntity.getBody()));


    }

    @DeleteMapping("/deleteSongPlaylist/{playlistId}")
    private Mono<ResponseEntity<PlaylistDTO>> deleteSongPlaylist(@PathVariable String playlistId, @RequestParam(name="songID") String songID){
        System.out.println(playlistId);
        System.out.println(songID);
        return iSongService.findSongById(songID)
                .flatMap(songDTOResponseEntity -> playlistService.deleteSongPlaylist(playlistId,songDTOResponseEntity.getBody()));


    }


/*    getStatusCode().is4xxClientError()) ?
            return  Mono.just(new ResponseEntity<>(new PlaylistDTO() ,HttpStatus.NOT_FOUND))
            : playlistService.addSongPlaylist(playlistId, iSongService.findSongById(songID)
            .flatMap(songDTOResponseEntity2 -> songDTOResponseEntity2.getBody()));*/


    //DELETE
    @DeleteMapping("/deletePlaylist/{id}")
    private Mono<ResponseEntity<String>> deletePlaylist(@PathVariable String id){
        return playlistService.deletePlaylist(id);
    }

}
