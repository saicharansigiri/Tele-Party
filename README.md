### 📽️ Task 1 Video Recording

[Click here to watch Task 1](https://drive.google.com/file/d/14EPj9Y0_0saZ8Nf2-IE4-dqbKBvdO5ed/view?usp=sharing)


### 📽️ Task 2 Demo Video Recording

[Click here to watch Task 2](https://drive.google.com/file/d/1kaL3OYQasBVij-aHy7pARTLh8I2jgehv/view?usp=sharing)


# TeleParty Implementation

This repository contains the implementation of the **Teleparty Android developer assignment**, consisting of two main tasks:

## Task 1: DRM-Protected Media Playback

### Implementation Details

The application implements a DRM-protected media player using Android's ExoPlayer library with the following features:

- **Architecture:** MVVM with Jetpack Compose UI  
- **Core Technologies:**
  - ExoPlayer `1.2.1` for media playback  
  - Widevine DRM integration  
  - DASH streaming protocol support  

### Technical Implementation

- **Media Source:**  
  The app plays content from  
  `https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd`

- **DRM Configuration:**  
  Uses license server at  
  `https://cwip-shaka-proxy.appspot.com/no_auth`

- **Video Resolution Selection:**  
  Dynamically extracts available track information from the media source and allows users to select from available resolutions

- **Player Controls:**  
  Provides standard media controls including play/pause and scrubbing functionality

- **State Management:**  
  Handles different states (loading, error, playback) through a dedicated `ViewModel`



---

## Task 2: Video Metadata Retrieval

### Implementation Details

A separate module that reverse-engineers and implements a non-public API to retrieve video metadata:

- **Architecture:** MVVM with Compose UI for display  
- **API Implementation:** Interfaces with MX Player's non-public API (`api.mxplayer.in`)

### Core Technologies

- Retrofit for network calls  
- OkHttp for HTTP client with custom headers  
- Hilt for dependency injection

### Technical Implementation

- **API Endpoint:** `/v1/web/detail/video`  
- **Authentication:** Custom headers to mimic browser requests  
- **User Interface:** Provides both manual video ID input and a dropdown selection of predefined videos  
- **Metadata Display:** Shows title, description, release date, duration, genres, and languages

---

## Dependencies

### Common Dependencies

- Kotlin `1.9.x`  
- Coroutines for asynchronous operations  
- Jetpack Compose for UI  
- Hilt for dependency injection  
- Retrofit and OkHttp for networking  

### Task 1 Specific Dependencies

- `androidx.media3:media3-exoplayer:1.2.1`  
- `androidx.media3:media3-exoplayer-dash:1.2.1`  
- `androidx.media3:media3-ui:1.2.1`  
- `androidx.media3:media3-exoplayer-hls:1.2.1`  

### Task 2 Specific Dependencies

- Retrofit with Gson converter  
- OkHttp logging interceptor for debugging  

---

## Project Structure

- `/app` - Contains Task 1 implementation (DRM media playback)  
- `/videometadata` - Contains Task 2 implementation (Metadata retrieval)  

---

## Technical Approach

### Task 1 Approach

- Created a custom `VideoPlayerViewModel` to manage ExoPlayer state and track selection  
- Implemented track extraction to identify available video resolutions  
- Built a Compose UI that adapts to the video's aspect ratio  
- Added resolution selection dropdown for quality selection  

### Task 2 Approach

- Reverse-engineered MX Player's API by analyzing network traffic  
- Implemented the appropriate headers to successfully authenticate requests  
- Created a data model that maps to the API response  
- Built a simple UI for entering video IDs and displaying results  

---

## Requirements Met

- Both tasks are implemented in separate modules with clean architecture  
- Task 1 successfully plays DRM-protected content with resolution selection  
- Task 2 retrieves metadata from a non-public API without using API keys  
- All required functionalities are implemented and demonstrated in recorded videos  
