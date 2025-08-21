export const AuthGuard = () => {
    console.log('AuthGuard')
    console.log(localStorage.getItem('user-token'))

    return localStorage.getItem('user-token') === null
}
