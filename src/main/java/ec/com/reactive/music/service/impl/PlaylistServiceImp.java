package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.AlbumDTO;
import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Album;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.domain.entities.Song;
import ec.com.reactive.music.repository.IAlbumRepository;
import ec.com.reactive.music.repository.IPlaylistRepository;
import ec.com.reactive.music.service.IAlbumService;
import ec.com.reactive.music.service.IPlaylistService;
import ec.com.reactive.music.service.ISongService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PlaylistServiceImp implements IPlaylistService {
    @Autowired
    private IPlaylistRepository iPlaylistRepository;

    @Autowired
    private ModelMapper modelMapper;




    @Override
    public Mono<ResponseEntity<Flux<PlaylistDTO>>> findAllPlaylists() {
        return this.iPlaylistRepository
                .findAll()
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NO_CONTENT.toString())))
                .map(album -> entityToDTO(album))
                .collectList()
                .map(playlistDTOS -> new ResponseEntity<>(Flux.fromIterable(playlistDTOS),HttpStatus.FOUND))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(Flux.empty(),HttpStatus.NO_CONTENT)));

    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> findPlaylistById(String id) {
        //Handling errors
        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .map(this::entityToDTO)
                .map(playlistDTO -> new ResponseEntity<>(playlistDTO, HttpStatus.FOUND))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }



    public Mono<ResponseEntity<PlaylistDTO>> addSongPlaylist(String playlistId,SongDTO songDTO){
        return iPlaylistRepository
                .findById(playlistId)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> {
                    List<Song> songs = playlist.getSongs();
                    Song song = modelMapper.map(songDTO, Song.class);

                        songs.add(song);
                        LocalTime songDuration = song.getDuration();
                        LocalTime playlistDuration = playlist.getDuration()
                                .plusHours(songDuration.getHour())
                                .plusMinutes(songDuration.getMinute())
                                .plusSeconds(songDuration.getSecond());
                        playlist.setDuration(playlistDuration);

                    return iPlaylistRepository.save(playlist);
                })
                .map(this::entityToDTO)
                .map(playlistDTO -> new ResponseEntity<>(playlistDTO, HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));

    }




    public Mono<ResponseEntity<PlaylistDTO>> deleteSongPlaylist(String playlistId,SongDTO songDTO){
        return iPlaylistRepository
                .findById(playlistId)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> {
                    List<Song> songs = playlist.getSongs();
                    Song song = modelMapper.map(songDTO, Song.class);

                        songs.remove(song);
                        LocalTime songDuration = song.getDuration();
                        LocalTime playlistDuration = playlist.getDuration()
                                .minusHours(songDuration.getHour())
                                .minusMinutes(songDuration.getMinute())
                                .minusSeconds(songDuration.getSecond());
                        playlist.setDuration(playlistDuration);


                    return iPlaylistRepository.save(playlist);
                })
                .map(this::entityToDTO)
                .map(playlistDTO -> new ResponseEntity<>(playlistDTO, HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST)));

    }











    @Override
    public Mono<ResponseEntity<PlaylistDTO>> savePlaylist(PlaylistDTO playlistDTO) {
        return this.iPlaylistRepository
                .save(DTOToEntity(playlistDTO))
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.EXPECTATION_FAILED.toString())))
                .map(playlist -> entityToDTO(playlist))
                .map(savedPlaylistDTO -> new ResponseEntity<>(savedPlaylistDTO,HttpStatus.CREATED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> updatePlaylist(String id, PlaylistDTO playlistDTO) {

        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> {
                    playlistDTO.setIdPlaylist(playlist.getIdPlaylist());
                    return this.savePlaylist(playlistDTO);
                })
                .map(playlistDTOResponseEntity -> new ResponseEntity<>(playlistDTOResponseEntity.getBody(),HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_MODIFIED)));
    }






    @Override
    public Mono<ResponseEntity<String>> deletePlaylist(String idPlaylist) {
        return this.iPlaylistRepository
                .findById(idPlaylist)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> this.iPlaylistRepository
                        .deleteById(playlist.getIdPlaylist())
                        .map(monoVoid -> new ResponseEntity<>(idPlaylist, HttpStatus.ACCEPTED)))
                .thenReturn(new ResponseEntity<>(idPlaylist, HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));

    }








    @Override
    public Playlist DTOToEntity(PlaylistDTO playlistDTO) {
        return this.modelMapper.map(playlistDTO, Playlist.class);
    }

    @Override
    public PlaylistDTO entityToDTO(Playlist playlist) {
        return this.modelMapper.map(playlist,PlaylistDTO.class);
    }



}
