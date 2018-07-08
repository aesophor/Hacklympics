from hacklympics.exceptions import AlreadyLoggedIn, NotLoggedIn
from hacklympics.events.events import *
from hacklympics.events.dispatcher import *
from hacklympics.models import *

class OnlineUsers:
    users = []

    @staticmethod
    def add(user: User):
        if not OnlineUsers.has(user):
            # The SocketServer which listens for events will start
            # only after the user has SUCCESSFULLY logged in!
            # Hence, the order of the following two lines of code
            # DOES matter. If we reverse their order, the dispatcher
            # will get a connection refused error
            # (since the SocketServer has not yet started) :)
            dispatch(LoginEvent(user), OnlineUsers.users)
            OnlineUsers.users.append(user)
        else:
            raise AlreadyLoggedIn("This user has already logged in.")

    @staticmethod
    def remove(user: User):
        if OnlineUsers.has(user):
            # Remove the user from OnlineUsers list first,
            # so that the server will not dispatch this event to 
            # the logging out user whose SocketServer
            # has already shutdown.
            OnlineUsers.users.remove(user)
            dispatch(LogoutEvent(user), OnlineUsers.users)
        else:
            raise NotLoggedIn("This user has already logged out.")

    @staticmethod
    def update(**kwargs):
        # Fetch the username from the varargs.
        user = [value[1] for value in kwargs.items() if value[0] == "user"][0]
        
        # Update that user's data.
        for value in kwargs.items():
            if value[0] == "user":
                pass
            else:
                exec("user.{0} = \"{1}\"".format(value[0], value[1]))
        
        user.save()

    @staticmethod
    def has(user):
        return True if OnlineUsers.get(user) else False
 
    @staticmethod
    def get(user: User):
        return [u for u in OnlineUsers.users if u == user]
    
    @staticmethod
    def get_all(role: str):
        return [user for user in OnlineUsers.users if user.role == role] 

    @staticmethod
    def show():
        print(OnlineUsers.users)



class OngoingExams:
    # Key: "teacher username"+"exam id"
    # Value: a list of students currently in the exam.
    exams = {}

    @staticmethod
    def add(exam: Exam):
        if not OngoingExams.has(exam):
            # Notify all users that the exam has been launched.
            dispatch(LaunchExamEvent(exam), OnlineUsers.users)
            
            # Create an empty student list for this exam.
            OngoingExams[exam] = ExamParticipants()
        else:
            raise AlreadyLaunched("This exam has already been launched.")

    @staticmethod
    def remove(exam: Exam):
        if OngoingExams.has(exam):
            # Remove the element from the dict.
            del OngoingExams.exams[exam]
            
            # Notify all users that the exam has been halt.
            dispatch(HaltExamEvent(exam), OnlineUsers.users)
        else:
            raise NotLaunched("This exam has not been launched or already ended.")

    @staticmethod
    def has(exam: Exam):
        return OngoingExams.exams.has_key(exam)

    @staticmethod
    def get(exam: Exam):
        if OngoingExams.has(exam):
            return OngoingExams.exams[e]
        else:
            raise NotLaunched("This exam has not been launched or already ended.")

    @staticmethod
    def show():
        print(OngoingExams.exams)


class ExamParticipants:
    # A participant of an exam can either be a student or a teacher.
    # Students take the exam while teacher proctor the exam.
    def __init__(self):
        self.students = []
        self.teachers = []

    def add(self, user: User):
        if user.is_student:
            self.students.append(user)
        else:
            self.teachers.append(user)

    def remove(self, user: User):
        if user.is_student:
            self.students.remove(user)
        else:
            self.teachers.remove(user)
