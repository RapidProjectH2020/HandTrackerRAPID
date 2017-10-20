#pragma once

#include <jni.h>
#include <Core/Types.hpp>
#include <boost/format.hpp>
#include <Core/Exception/Exception.hpp>

namespace JNI
{

using namespace MBV::Core;

template < class T >
struct JavaFormat;

template < class T >
struct Object
{
    jobject obj;
};

template < class T >
struct JavaFormat< Object<T> >
{
    static const char* get()
    {
        static char buff[2048];
        sprintf(buff, "L%s;", GetClassString((Object<T>*)NULL));
        return buff;
    }
};

template <>
struct JavaFormat<Byte>
{
	static const char* get() { return "B"; }
};

template <>
struct JavaFormat<UByte>
	: public JavaFormat<Byte>
{
};

template <>
struct JavaFormat<Short>
{
	static const char* get() { return "S"; }
};

template <>
struct JavaFormat<UShort>
	: public JavaFormat<Short>
{
};

template <>
struct JavaFormat<Int>
{
	static const char* get() { return "I"; }
};

template <>
struct JavaFormat<Long>
{
    static const char* get() { return "J"; }
};

template <>
struct JavaFormat<Single>
{
	static const char* get() { return "F"; }
};

template <>
struct JavaFormat<Double>
{
	static const char* get() { return "D"; }
};

template < class T >
struct JavaFormat< std::vector<T> >
{
	static const char* get()
	{
		static char buff[16];
		sprintf(buff, "[%s", JavaFormat<T>::get());
		return buff;
	}
};

template < class T >
struct JavaGetSet;

template < class T >
struct JavaGetSet< Object<T> >
{
    typedef Object<T> Obj;

    static void get(JNIEnv *env, jobject obj, jfieldID field, Obj &value)
    {
        value.obj = env->GetObjectField(obj, field);
    }

    static void set(JNIEnv *env, jobject obj, jfieldID field, Obj value)
    {
        env->SetObjectField(obj, field, value.obj);
    }
};

template <>
struct JavaGetSet<Int>
{
	static void get(JNIEnv *env, jobject obj, jfieldID field, int &value)
	{
		value = env->GetIntField(obj, field);
	}

	static void set(JNIEnv *env, jobject obj, jfieldID field, int value)
	{
		env->SetIntField(obj, field, value);
	}
};

template <>
struct JavaGetSet<Long>
{
    static void get(JNIEnv *env, jobject obj, jfieldID field, Long &value)
    {
        value = env->GetLongField(obj, field);
    }

    static void set(JNIEnv *env, jobject obj, jfieldID field, Long value)
    {
        env->SetLongField(obj, field, value);
    }
};

template <>
struct JavaGetSet<Single>
{
    static void get(JNIEnv *env, jobject obj, jfieldID field, Single &value)
    {
        value = env->GetFloatField(obj, field);
    }

    static void set(JNIEnv *env, jobject obj, jfieldID field, Single value)
    {
        env->SetFloatField(obj, field, value);
    }
};

template < class T >
struct JavaArrayType;

template < class Elem, class Array >
struct JavaArrayTypeBase
{
	typedef Array ArrayType;
	typedef Elem ElemType;
	typedef ArrayType(JNIEnv_::*Allocator)(jsize length);
	typedef void(JNIEnv_::*Getter)(ArrayType, jsize, jsize, ElemType*);
	typedef void(JNIEnv_::*Setter)(ArrayType, jsize, jsize, const ElemType*);
};

template <>
struct JavaArrayType<Byte>
	: public JavaArrayTypeBase<jbyte, jbyteArray>
{
	static Allocator getAllocator() { return &JNIEnv_::NewByteArray; }
	static Getter getGetter() { return &JNIEnv_::GetByteArrayRegion; }
	static Setter getSetter() { return &JNIEnv_::SetByteArrayRegion; }
};

template <>
struct JavaArrayType<UByte>
	: public JavaArrayType<Byte>
{
};

template <>
struct JavaArrayType<Short>
	: public JavaArrayTypeBase<jshort, jshortArray>
{
	static Allocator getAllocator() { return &JNIEnv_::NewShortArray; }
	static Getter getGetter() { return &JNIEnv_::GetShortArrayRegion; }
	static Setter getSetter() { return &JNIEnv_::SetShortArrayRegion; }
};

template <>
struct JavaArrayType<UShort>
	: public JavaArrayType<Short>
{
};

template <>
struct JavaArrayType<Single>
	: public JavaArrayTypeBase<jfloat, jfloatArray>
{
	static Allocator getAllocator() { return &JNIEnv_::NewFloatArray; }
	static Getter getGetter() { return &JNIEnv_::GetFloatArrayRegion; }
	static Setter getSetter() { return &JNIEnv_::SetFloatArrayRegion; }
};

template <>
struct JavaArrayType<Double>
	: public JavaArrayTypeBase<jdouble, jdoubleArray>
{
	static Allocator getAllocator() { return &JNIEnv_::NewDoubleArray; }
	static Getter getGetter() { return &JNIEnv_::GetDoubleArrayRegion; }
	static Setter getSetter() { return &JNIEnv_::SetDoubleArrayRegion; }
};

template < class T >
struct JavaGetSet< std::vector<T> >
	: public JavaArrayType<T>
{
	typedef std::vector<T> Vec;
	typedef JavaArrayType<T> Base;
	typedef typename Base::ArrayType ArrayType;
	typedef typename Base::ElemType ElemType;

	static ArrayType read(JNIEnv *env, jobject obj, Vec &value)
	{
		ArrayType array = static_cast<ArrayType>(obj);
		if (array)
		{
			jsize length = env->GetArrayLength(array);
			value.resize(length);
			(env->*Base::getGetter())(array, 0, value.size(), reinterpret_cast<ElemType*>(value.data()));
		}
		return array;
	}

	static ArrayType write(JNIEnv *env, const Vec &value)
	{
		ArrayType array = (env->*Base::getAllocator())(value.size());
		(env->*Base::getSetter())(array, 0, value.size(), reinterpret_cast<const ElemType*>(value.data()));
		return array;
	}

	static void get(JNIEnv *env, jobject obj, jfieldID field, Vec &value)
	{
		read(env, env->GetObjectField(obj, field), value);
	}

	static void set(JNIEnv *env, jobject obj, jfieldID field, const Vec &value)
	{
		ArrayType array = write(env, value);
		env->SetObjectField(obj, field, array);
	}
};

struct JNIHelper
{
	JNIHelper(JNIEnv *env, jobject obj)
	: env(env)
	, obj(obj)
	{}

	template < class T >
	jfieldID getField(const String &field) const
	{
		jclass theClass = env->GetObjectClass(obj);
		MBV_ASSERT_MSG(theClass, "Object's class could not be retrieved");
		const char *format = JavaFormat<T>::get();
		MBV_ASSERT_MSG(format, "Field format could not be generated");
		jfieldID ret = env->GetFieldID(theClass, field.c_str(), format);
		MBV_ASSERT_MSG(ret, boost::str(boost::format("Field '%s' with format '%s' not found") % field % format));
		return ret;
	}

	template < class T >
	void get(const String &field, T &value) const
	{
		JavaGetSet<T>::get(env, obj, getField<T>(field), value);
	}

	template < class T >
	T get(const String &field) const
	{
		T ret;
		get(field, ret);
		return ret;
	}

	template < class T >
	void set(const String &field, const T &value) const
	{
		JavaGetSet<T>::set(env, obj, getField<T>(field), value);
	}

    Matrix4x4 getMatrix(const String &name) const
    {
        Vector<Single>::Type data;
        get(name, data);
        Matrix4x4 ret;
        std::copy(data.begin(), data.end(), &ret[0][0]);
        return ret;
    }

    void setMatrix(const String &name, const Matrix4x4 &mat)
    {
        Vector<Single>::Type data(16);
        std::copy(&mat[0][0], &mat[0][0] + 16, data.begin());
        set(name, data);
    }

    template<class T>
    cv::Mat_<T> getMat(const String &name, Int width) const
    {
        typedef cv::DataType<T> DT;
        typedef typename DT::value_type ValueType;
        typedef typename DT::channel_type ElemType;
        static const int channels = sizeof(ValueType) / sizeof(ElemType);
        typename Vector<ElemType>::Type data;
        get(name, data);
        cv::Mat_<T> ret(cv::Size(width, data.size() / (channels * width)));
        std::copy(data.begin(), data.end(), ret.template ptr<ElemType>());
        return ret;
    }

    template<class T>
    void setMat(const String &name, const cv::Mat_<T> &mat)
    {
        typedef cv::DataType<T> DT;
        typedef typename DT::value_type ValueType;
        typedef typename DT::channel_type ElemType;
        static const int channels = sizeof(ValueType) / sizeof(ElemType);
        typename Vector<ElemType>::Type data(mat.total() * channels);
        std::copy(mat.template ptr<ElemType>(),
                mat.template ptr<ElemType>() + data.size(), data.begin());
        set(name, data);
    }
private:
	JNIEnv *env;
	jobject obj;
};


}
