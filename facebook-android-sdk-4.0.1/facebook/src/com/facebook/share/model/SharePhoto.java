/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.facebook.share.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a photo for sharing.
 *
 * Use {@link com.facebook.share.model.SharePhoto.Builder} to build instances
 */
public final class SharePhoto implements ShareModel {
    private final Bitmap bitmap;
    private final Uri imageUrl;
    private final boolean userGenerated;

    private SharePhoto(final Builder builder) {
        this.bitmap = builder.bitmap;
        this.imageUrl = builder.imageUrl;
        this.userGenerated = builder.userGenerated;
    }

    SharePhoto(final Parcel in) {
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.imageUrl = in.readParcelable(Uri.class.getClassLoader());
        this.userGenerated = (in.readByte() != 0);
    }

    /**
     * If the photo is resident in memory, this method supplies the data.
     * @return {@link android.graphics.Bitmap} representation of the photo.
     */
    @Nullable
    public Bitmap getBitmap() {
        return this.bitmap;
    }

    /**
     * The URL to the photo.
     * @return {@link android.net.Uri} that points to a network location or the location of the
     * photo on disk.
     */
    @Nullable
    public Uri getImageUrl() {
        return this.imageUrl;
    }

    /**
     * Specifies whether the photo represented by this object was generated by the user or by the
     * application.
     * @return Indication of whether the photo is user-generated.
     */
    public boolean getUserGenerated() {
        return this.userGenerated;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel out, final int flags) {
        out.writeParcelable(this.bitmap, 0);
        out.writeParcelable(this.imageUrl, 0);
        out.writeByte((byte)(this.userGenerated ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Creator<SharePhoto> CREATOR = new Creator<SharePhoto>() {
        public SharePhoto createFromParcel(final Parcel in) {
            return new SharePhoto(in);
        }

        public SharePhoto[] newArray(final int size) {
            return new SharePhoto[size];
        }
    };

    /**
     * Builder for the {@link com.facebook.share.model.SharePhoto} interface.
     */
    public static final class Builder implements ShareModelBuilder<SharePhoto, Builder> {
        private Bitmap bitmap;
        private Uri imageUrl;
        private boolean userGenerated;

        /**
         * Sets the bitmap representation of the photo.
         * @param bitmap {@link android.graphics.Bitmap} representation of the photo.
         * @return The builder.
         */
        public Builder setBitmap(@Nullable final Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        /**
         * Sets the URL to the photo.
         * @param imageUrl {@link android.net.Uri} that points to a network location or the location
         *                                        of the photo on disk.
         * @return The builder.
         */
        public Builder setImageUrl(@Nullable final Uri imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        /**
         * Sets whether the photo represented by this object was generated by the user or by the
         * application.
         * @param userGenerated Indication of whether the photo is user-generated.
         * @return The builder.
         */
        public Builder setUserGenerated(final boolean userGenerated) {
            this.userGenerated = userGenerated;
            return this;
        }

        // Accessor that is present only for ShareVideoContent(Parcel) to use
        Uri getImageUrl() {
            return imageUrl;
        }

        // Accessor that is present only for ShareVideoContent(Parcel) to use
        Bitmap getBitmap() {
            return bitmap;
        }

        @Override
        public SharePhoto build() {
            return new SharePhoto(this);
        }

        @Override
        public Builder readFrom(final SharePhoto model) {
            if (model == null) {
                return this;
            }
            return this
                    .setBitmap(model.getBitmap())
                    .setImageUrl(model.getImageUrl())
                    .setUserGenerated(model.getUserGenerated())
                    ;
        }

        @Override
        public Builder readFrom(final Parcel parcel) {
            return this.readFrom(
                    (SharePhoto)parcel.readParcelable(SharePhoto.class.getClassLoader()));
        }

        public static void writeListTo(final Parcel out, final List<SharePhoto> photos) {
            final List<SharePhoto> list = new ArrayList<>();
            for (SharePhoto photo : photos) {
                list.add(photo);
            }
            out.writeTypedList(list);
        }

        public static List<SharePhoto> readListFrom(final Parcel in) {
            final List<SharePhoto> list = new ArrayList<>();
            in.readTypedList(list, CREATOR);
            return list;
        }
    }
}
