package com.sopian.imageapp.core.utils

import com.sopian.imageapp.core.data.source.local.entity.InfoEntity
import com.sopian.imageapp.core.data.source.local.entity.PhotoEntity
import com.sopian.imageapp.core.data.source.remote.response.DownloadResponse
import com.sopian.imageapp.core.data.source.remote.response.InfoResponse
import com.sopian.imageapp.core.data.source.remote.response.PhotoResponse
import com.sopian.imageapp.core.domain.model.Download
import com.sopian.imageapp.core.domain.model.Info
import com.sopian.imageapp.core.domain.model.Photo

object DataMapper {

    fun mapResponsesToEntities(input: List<PhotoResponse>): List<PhotoEntity> {
        val phototList = ArrayList<PhotoEntity>()

        input.map { photoResponse ->
            val unsplashPhotoUrls = photoResponse.urls.let {
                PhotoEntity.UnsplashPhotoUrls(
                    it.small, it.regular
                )
            }
            val profileImage = photoResponse.user.profileImage.let {
                PhotoEntity.User.ProfileImage(
                    it.medium
                )
            }
            val user = photoResponse.user.let {
                PhotoEntity.User(
                    it.id,
                    it.username,
                    profileImage
                )
            }
            val photo = PhotoEntity(
                id = photoResponse.id,
                description = photoResponse.description,
                urls = unsplashPhotoUrls,
                isFavorite = false,
                created_at = photoResponse.created_at,
                query = photoResponse.query,
                user = user
            )
            phototList.add(photo)
        }
        return phototList
    }

    fun mapEntitiesToDomain(input: List<PhotoEntity>) : List<Photo> =
        input.map { photoEntity ->
            val unsplashPhotoUrls = photoEntity.urls.let {
                Photo.UnsplashPhotoUrls(
                    it.small, it.regular
                )
            }
            val profileImage = photoEntity.user.profileImage.let {
                Photo.User.ProfileImage(
                    it.medium
                )
            }
            val user = photoEntity.user.let {
                Photo.User(
                    it.id,
                    it.username,
                    profileImage
                )
            }
            Photo(
                id = photoEntity.id,
                description = photoEntity.description,
                urls = unsplashPhotoUrls,
                isFavorite = photoEntity.isFavorite,
                created_at = photoEntity.created_at,
                query = photoEntity.query!!,
                user = user
            )
        }

    fun mapDomainToEntity(input: Photo): PhotoEntity {
        val unsplashPhotoUrls = input.urls.let {
            PhotoEntity.UnsplashPhotoUrls(
                it.small, it.regular
            )
        }
        val profileImage = input.user.profileImage.let {
            PhotoEntity.User.ProfileImage(
                it.medium
            )
        }
        val user = input.user.let {
            PhotoEntity.User(
                it.id,
                it.username,
                profileImage
            )
        }
        return PhotoEntity(
            id = input.id,
            description = input.description,
            urls = unsplashPhotoUrls,
            isFavorite = input.isFavorite,
            created_at = input.created_at,
            query = input.query,
            user = user
        )
    }

    fun mapEntityToDomain(input: PhotoEntity) : Photo {
        val unsplashPhotoUrls = input.urls.let {
            Photo.UnsplashPhotoUrls(
                it.small, it.regular
            )
        }
        val profileImage = input.user.profileImage.let {
            Photo.User.ProfileImage(
                it.medium
            )
        }
        val user = input.user.let {
            Photo.User(
                it.id,
                it.username,
                profileImage
            )
        }

        return Photo(
            id = input.id,
            description = input.description,
            urls = unsplashPhotoUrls,
            isFavorite = input.isFavorite,
            created_at = input.created_at,
            query = input.query!!,
            user = user
        )
    }

    fun mapDownloadResponseToDomain(input: DownloadResponse?) : Download {
        return Download(input?.url)
    }

    fun mapInfoResponseToEntity(input: InfoResponse): InfoEntity {
        val exif = input.exif.let {
            InfoEntity.ExifEntity(
                make = it?.make,
                model = it?.model,
                exposure_time = it?.exposure_time,
                aperture = it?.aperture,
                focal_length = it?.focal_length,
                iso = it?.iso
            )
        }

        val position = input.location?.position.let {
            InfoEntity.LocationEntity.PositionEntity(
                latitude = it?.latitude,
                longitude = it?.longitude
            )
        }

        val location = input.location.let {
            InfoEntity.LocationEntity(
                title = it?.title,
                name = it?.name,
                city = it?.city,
                country = it?.country,
                position = position
            )
        }
        return InfoEntity(
            id = input.id,
            createdAt = input.createdAt,
            width = input.width,
            height = input.height,
            exif = exif,
            location = location,
            views = input.views,
            downloads = input.downloads
        )
    }

    fun mapInfoEntityToDomain(input: InfoEntity?) : Info{
        val exif = input?.exif.let {
            Info.Exif(
                make = it?.make,
                model = it?.model,
                exposure_time = it?.exposure_time,
                aperture = it?.aperture,
                focal_length = it?.focal_length,
                iso = it?.iso
            )
        }

        val position = input?.location?.position.let {
            Info.Location.Position(
                latitude = it?.latitude,
                longitude = it?.longitude
            )
        }

        val location = input?.location.let {
            Info.Location(
                title = it?.title,
                name = it?.name,
                city = it?.city,
                country = it?.country,
                position = position
            )
        }

        val id = input?.id ?: ""
        val createdAt = input?.createdAt ?: ""

        return Info(
            id = id,
            createdAt = createdAt,
            width = input?.width,
            height = input?.height,
            exif = exif,
            location = location,
            views = input?.views,
            downloads = input?.downloads
        )
    }
}