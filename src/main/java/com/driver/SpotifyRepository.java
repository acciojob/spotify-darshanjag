package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        for(User curUser: users){
            if(curUser.getMobile().equals(mobile)){
                return curUser;
            }
        }
        User u = new User(name,mobile);
        users.add(u);
        return u;
    }

    public Artist createArtist(String name) {
        for(Artist artist: artists){
            if(artist.getName().equals(name))
                return artist;
        }
        Artist a = new Artist(name);
        artists.add(a);
        return a;
    }

    public Album createAlbum(String title, String artistName) {

        for(Album album : albums){
            if(album.getTitle().equals(title))
                return  album;
        }
        Album newAlbum = new Album(title);
        albums.add(newAlbum);
        List<Album> albumList = new ArrayList<>();

        boolean artistExists = false;

        for(Artist artist: artists){
            if(artist.getName().equals(artistName)){
                artistExists= true;
                if(artistAlbumMap.containsKey(artist)){
                    albumList = artistAlbumMap.get(artist);
                }
                albumList.add(newAlbum);
                artistAlbumMap.put(artist,albumList);

                break;
            }
        }
        if(!artistExists){
            Artist newArtist = new Artist(artistName);
            artists.add(newArtist);

            artistAlbumMap.put(newArtist,albumList);
        }

        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{


        List<Song> songList = new ArrayList<>();

        for(Album album: albums){

            if(album.getTitle().equals(albumName)){
                Song newSong = new Song(title, length);
                songs.add(newSong);
                if(albumSongMap.containsKey(album)){
                    songList = albumSongMap.get(album);
                }
                songList.add(newSong);
                albumSongMap.put(album,songList);

                return newSong;
            }else throw new Exception("Album does not exist");

        }
        return new Song();
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                Playlist newPlaylist = new Playlist(title);
                playlists.add(newPlaylist);
                List<Song>songList= new ArrayList<>();
                List<User>listenerList = new ArrayList<>();
                List<Playlist> playlistList = new ArrayList<>();
                for(Song song: songs){
                    if(song.getLength()==length){
                        songList.add(song);
                    }
                }
                if(playlistListenerMap.containsKey(newPlaylist)){
                    listenerList = playlistListenerMap.get(newPlaylist);
                }
                if(userPlaylistMap.containsKey(user)){
                   playlistList = userPlaylistMap.get(user);
                }
                listenerList.add(user);
                playlistSongMap.put(newPlaylist,songList);
                playlistListenerMap.put(newPlaylist,listenerList);
                creatorPlaylistMap.put(user,newPlaylist);
                userPlaylistMap.put(user,playlistList);
                return newPlaylist;
            }else throw new Exception("User does not exist");
        }
       return new Playlist();
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(title))
                return  playlist;
        }
        Playlist playlist = new Playlist(title);
        // adding playlist to playlists list
        playlists.add(playlist);

        List<Song> temp= new ArrayList<>();
        for(Song song : songs){
            if(songTitles.contains(song.getTitle())){
                temp.add(song);
            }
        }
        playlistSongMap.put(playlist,temp);

        User curUser= new User();
        boolean flag= false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser=user;
                flag= true;
                break;
            }
        }
        if (flag==false){
            throw new Exception("User does not exist");
        }

        List<User> userslist = new ArrayList<>();
        if(playlistListenerMap.containsKey(playlist)){
            userslist=playlistListenerMap.get(playlist);
        }
        userslist.add(curUser);
        playlistListenerMap.put(playlist,userslist);

        creatorPlaylistMap.put(curUser,playlist);

        List<Playlist>userplaylists = new ArrayList<>();
        if(userPlaylistMap.containsKey(curUser)){
            userplaylists=userPlaylistMap.get(curUser);
        }
        userplaylists.add(playlist);
        userPlaylistMap.put(curUser,userplaylists);

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        boolean flag =false;
        Playlist playlist = new Playlist();
        for(Playlist curplaylist: playlists){
            if(curplaylist.getTitle().equals(playlistTitle)){
                playlist=curplaylist;
                flag=true;
                break;
            }
        }
        if (flag==false){
            throw new Exception("Playlist does not exist");
        }

        User curUser= new User();
        boolean flag2= false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser=user;
                flag2= true;
                break;
            }
        }
        if (flag2==false){
            throw new Exception("User does not exist");
        }
//        public HashMap<Playlist, List<User>> playlistListenerMap;
        List<User> userslist = new ArrayList<>();
        if(playlistListenerMap.containsKey(playlist)){
            userslist=playlistListenerMap.get(playlist);
        }
        if(!userslist.contains(curUser))
            userslist.add(curUser);
        playlistListenerMap.put(playlist,userslist);
//        public HashMap<User, Playlist> creatorPlaylistMap;
        if(creatorPlaylistMap.get(curUser)!=playlist)
            creatorPlaylistMap.put(curUser,playlist);
//        public HashMap<User, List<Playlist>> userPlaylistMap;
        List<Playlist>userplaylists = new ArrayList<>();
        if(userPlaylistMap.containsKey(curUser)){
            userplaylists=userPlaylistMap.get(curUser);
        }
        if(!userplaylists.contains(playlist))userplaylists.add(playlist);
        userPlaylistMap.put(curUser,userplaylists);


        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User curUser= new User();
        boolean flag2= false;
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                curUser=user;
                flag2= true;
                break;
            }
        }
        if (flag2==false){
            throw new Exception("User does not exist");
        }

        Song song = new Song();
        boolean flag = false;
        for(Song cursong : songs){
            if(cursong.getTitle().equals(songTitle)){
                song=cursong;
                flag=true;
                break;
            }
        }
        if (flag==false){
            throw new Exception("Song does not exist");
        }

        //public HashMap<Song, List<User>> songLikeMap;
        List<User> users = new ArrayList<>();
        if(songLikeMap.containsKey(song)){
            users=songLikeMap.get(song);
        }
        if (!users.contains(curUser)){
            users.add(curUser);
            songLikeMap.put(song,users);
            song.setLikes(song.getLikes()+1);


//            public HashMap<Album, List<Song>> albumSongMap;
            Album album = new Album();
            for(Album curAlbum : albumSongMap.keySet()){
                List<Song> temp = albumSongMap.get(curAlbum);
                if(temp.contains(song)){
                    album=curAlbum;
                    break;
                }
            }


//            public HashMap<Artist, List<Album>> artistAlbumMap;
            Artist artist = new Artist();
            for(Artist curArtist : artistAlbumMap.keySet()){
                List<Album> temp = artistAlbumMap.get(curArtist);
                if(temp.contains(album)){
                    artist=curArtist;
                    break;
                }
            }

            artist.setLikes(artist.getLikes()+1);
        }
        return song;
    }

    public String mostPopularArtist() {
//        public List<Artist> artists;
        String name="";
        int maxLikes = Integer.MIN_VALUE;
        for(Artist art : artists){
            maxLikes= Math.max(maxLikes,art.getLikes());
        }
        for(Artist art : artists){
            if(maxLikes==art.getLikes()){
                name=art.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
//        public List<Song> songs;
        String name="";
        int maxLikes = Integer.MIN_VALUE;
        for(Song song : songs){
            maxLikes=Math.max(maxLikes,song.getLikes());
        }
        for(Song song : songs){
            if(maxLikes==song.getLikes())
                name=song.getTitle();
        }
        return name;
    }
}
