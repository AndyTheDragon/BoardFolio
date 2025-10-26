package dat.dao;

import dat.exceptions.DaoException;

import java.util.List;

public interface CrudDAO
{
    <T> T create(T object) throws DaoException;
    <T> List<T> create(List<T> objects) throws DaoException;

    <T> T getById(Class<T> type, Object id) throws DaoException;
    <T> List<T> getAll(Class<T> type) throws DaoException;

    <T> T update(T object) throws DaoException;
    <T> List<T> update(List<T> objects) throws DaoException;

    <T> void delete(T object) throws DaoException;
    <T> void delete(Class<T> type, Object id) throws DaoException;
}
